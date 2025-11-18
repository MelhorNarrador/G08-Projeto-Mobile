package pt.iade.lane.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.theme.LaneTheme
import pt.iade.lane.ui.viewmodels.CreateEventViewModel
import pt.iade.lane.ui.viewmodels.EventCreationState
import java.math.BigDecimal
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
class CreateEventActivity : ComponentActivity() {

    private val eventoRepository = EventoRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val appInfo = packageManager.getApplicationInfo(packageName, android.content.pm.PackageManager.GET_META_DATA)
            val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
            if (!Places.isInitialized() && apiKey != null) {
                Places.initialize(applicationContext, apiKey)
            }
        } catch (e: Exception) {
            Log.e("PlacesInit", "Erro ao inicializar Places: ${e.message}")
        }

        sessionManager = SessionManager(applicationContext)

        setContent {
            LaneTheme {
                val viewModel: CreateEventViewModel = viewModel(
                    factory = CreateEventViewModel.Factory(eventoRepository, sessionManager)
                )
                val filters by viewModel.filters.collectAsState()
                val creationState by viewModel.creationState.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(Unit) { viewModel.loadFilters() }

                LaunchedEffect(creationState) {
                    when (val state = creationState) {
                        is EventCreationState.Success -> {
                            Toast.makeText(context, "Evento criado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is EventCreationState.Error -> {
                            Toast.makeText(context, "Erro: ${state.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Criar Evento") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    CreateEventScreen(
                        modifier = Modifier.padding(innerPadding),
                        isLoading = creationState is EventCreationState.Loading,
                        filterOptions = filters,
                        creatorId = viewModel.getCreatorId(),
                        onCreateClick = { eventDTO -> viewModel.createEvent(eventDTO) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    filterOptions: List<Filtro>,
    creatorId: Int?,
    onCreateClick: (CreateEventDTO) -> Unit
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("0") }
    var maxParticipantes by remember { mutableStateOf("100") }

    var localizacaoTexto by remember { mutableStateOf("") }
    var latitudeSelecionada by remember { mutableStateOf(BigDecimal.ZERO) }
    var longitudeSelecionada by remember { mutableStateOf(BigDecimal.ZERO) }

    var tipoEventoExpanded by remember { mutableStateOf(false) }
    var selectedFiltro by remember { mutableStateOf<Filtro?>(null) }
    var privacidadeExpanded by remember { mutableStateOf(false) }
    var selectedVisibilidade by remember { mutableStateOf("public") }
    val visibilidadeOptions = listOf("public", "private")
    var data by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    val placesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { intent ->
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    localizacaoTexto = place.address ?: place.name ?: "Localização"
                    place.latLng?.let {
                        latitudeSelecionada = BigDecimal.valueOf(it.latitude)
                        longitudeSelecionada = BigDecimal.valueOf(it.longitude)
                    }
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                result.data?.let { intent ->
                    val status = Autocomplete.getStatusFromIntent(intent)
                    Log.e("GooglePlaces", "Erro API: ${status.statusMessage}")
                    Toast.makeText(context, "Erro Google: ${status.statusMessage}", Toast.LENGTH_LONG).show()
                }
            }
            Activity.RESULT_CANCELED -> {
            }
        }
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(context, { _, y, m, d ->
        data = "$y-${(m + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    val timePickerDialog = TimePickerDialog(context, { _, h, m ->
        hora = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

    Column(modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {

        Text("Título do Evento", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Text("Descrição", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Localização", style = MaterialTheme.typography.labelMedium)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = localizacaoTexto,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Toque para pesquisar...") },
                trailingIcon = { Icon(Icons.Default.Place, "Mapa") },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        try {
                            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                                .build(context)
                            placesLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro ao abrir mapa: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Tipo de Evento", style = MaterialTheme.typography.labelMedium)
        ExposedDropdownMenuBox(expanded = tipoEventoExpanded, onExpandedChange = { tipoEventoExpanded = !tipoEventoExpanded }) {
            val textoDisplay = selectedFiltro?.nome ?: "Selecione um tipo"
            OutlinedTextField(value = textoDisplay, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoEventoExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true))
            if (filterOptions.isNotEmpty()) {
                ExposedDropdownMenu(expanded = tipoEventoExpanded, onDismissRequest = { tipoEventoExpanded = false }) {
                    filterOptions.forEach { filtro ->
                        DropdownMenuItem(text = { Text(text = filtro.nome ?: "Sem nome") }, onClick = { selectedFiltro = filtro; tipoEventoExpanded = false })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Data", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(value = data, onValueChange = {}, readOnly = true, trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.DateRange, "Data") } }, modifier = Modifier.fillMaxWidth())
            }
            Column(Modifier.weight(1f)) {
                Text("Hora", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(value = hora, onValueChange = {}, readOnly = true, trailingIcon = { IconButton(onClick = { timePickerDialog.show() }) { Icon(Icons.Default.AccessTime, "Hora") } }, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Preço (€)", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(value = preco, onValueChange = { preco = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
            }
            Column(Modifier.weight(1f)) {
                Text("Max. Participantes", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(value = maxParticipantes, onValueChange = { maxParticipantes = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Privacidade", style = MaterialTheme.typography.labelMedium)
        ExposedDropdownMenuBox(expanded = privacidadeExpanded, onExpandedChange = { privacidadeExpanded = !privacidadeExpanded }) {
            OutlinedTextField(value = selectedVisibilidade, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = privacidadeExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true))
            ExposedDropdownMenu(expanded = privacidadeExpanded, onDismissRequest = { privacidadeExpanded = false }) {
                visibilidadeOptions.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { selectedVisibilidade = option; privacidadeExpanded = false }) }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (selectedFiltro == null || titulo.isBlank() || data.isBlank() || hora.isBlank() || localizacaoTexto.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos (incluindo localização)", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val precoBigDecimal = try { BigDecimal(preco) } catch (e: Exception) { BigDecimal.ZERO }
                val maxPartInt = try { maxParticipantes.toInt() } catch (e: Exception) { 0 }
                val dataFinal = "${data}T${hora}:00"

                val eventDTO = CreateEventDTO(
                    titulo = titulo,
                    descricao = descricao,
                    visibilidade = selectedVisibilidade,
                    categoriaId = selectedFiltro!!.id,
                    criadorId = creatorId ?: 0,
                    localizacao = localizacaoTexto,
                    latitude = latitudeSelecionada,
                    longitude = longitudeSelecionada,
                    data = dataFinal,
                    preco = precoBigDecimal,
                    maxParticipantes = maxPartInt,
                    id = 0,
                    name = ""
                )
                onCreateClick(eventDTO)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Criar Evento")
        }
    }
}
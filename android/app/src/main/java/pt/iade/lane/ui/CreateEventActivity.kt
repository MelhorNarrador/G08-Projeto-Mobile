package pt.iade.lane.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.theme.LaneTheme
import pt.iade.lane.ui.viewmodels.CreateEventViewModel
import pt.iade.lane.ui.viewmodels.EventCreationState
import java.math.BigDecimal
import java.util.Calendar
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.IconButton

class CreateEventActivity : ComponentActivity() {

    private val eventoRepository = EventoRepository()
    private lateinit var sessionManager: SessionManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(applicationContext)

        setContent {
            LaneTheme {
                val viewModel: CreateEventViewModel = viewModel(
                    factory = CreateEventViewModel.Factory(eventoRepository, sessionManager)
                )
                val filters by viewModel.filters.collectAsState()
                val creationState by viewModel.creationState.collectAsState()
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    viewModel.loadFilters()
                }
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
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    CreateEventScreen(
                        modifier = Modifier.padding(innerPadding),
                        isLoading = creationState is EventCreationState.Loading,
                        filterOptions = filters,
                        onCreateClick = { eventDTO ->
                            viewModel.createEvent(eventDTO)
                        },
                        creatorId = viewModel.getCreatorId()
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
    // --- Estados (Fica tudo igual) ---
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("0") }
    var maxParticipantes by remember { mutableStateOf("100") }
    var tipoEventoExpanded by remember { mutableStateOf(false) }
    var selectedFiltro by remember { mutableStateOf<Filtro?>(null) }
    var privacidadeExpanded by remember { mutableStateOf(false) }
    var selectedVisibilidade by remember { mutableStateOf("public") }
    val visibilidadeOptions = listOf("public", "private", "invite")
    var data by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // --- Lógica dos Pickers (Fica tudo igual) ---
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            data = "$year-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay: Int, minute: Int ->
            hora = "${hourOfDay.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // 24-hour format
    )

    // --- UI (Aqui estão as correções) ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ... (Título, Descrição, Dropdown Tipo de Evento ficam iguais) ...
        Text("Título do Evento", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Text("Descrição", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Tipo de Evento", style = MaterialTheme.typography.labelMedium)
        ExposedDropdownMenuBox(
            expanded = tipoEventoExpanded,
            onExpandedChange = { tipoEventoExpanded = !tipoEventoExpanded }
        ) {
            OutlinedTextField(
                value = selectedFiltro?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = tipoEventoExpanded,
                onDismissRequest = { tipoEventoExpanded = false }
            ) {
                filterOptions.forEach { filtro ->
                    DropdownMenuItem(
                        text = { Text(filtro.nome) },
                        onClick = {
                            selectedFiltro = filtro
                            tipoEventoExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


        // --- Linha para Data e Hora (CORRIGIDA) ---
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Data", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = data,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("YYYY-MM-DD") },
                    // CORREÇÃO: Remove o .clickable e adiciona o trailingIcon
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Escolher Data")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(Modifier.weight(1f)) {
                Text("Hora", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = hora,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("--:--") },
                    // CORREÇÃO: Remove o .clickable e adiciona o trailingIcon
                    trailingIcon = {
                        IconButton(onClick = { timePickerDialog.show() }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Escolher Hora")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Localização", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(value = localizacao, onValueChange = { localizacao = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        // --- Linha para Preço e Max. Participantes (CORRIGIDA) ---
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Preço (€)", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = preco,
                    onValueChange = { preco = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, // <-- CORREÇÃO: Impede "enters"
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(Modifier.weight(1f)) {
                Text("Max. Participantes", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = maxParticipantes,
                    onValueChange = { maxParticipantes = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, // <-- CORREÇÃO: Impede "enters"
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // ... (Dropdown Privacidade e Botão Criar Evento ficam iguais) ...
        Text("Privacidade", style = MaterialTheme.typography.labelMedium)
        ExposedDropdownMenuBox(
            expanded = privacidadeExpanded,
            onExpandedChange = { privacidadeExpanded = !privacidadeExpanded }
        ) {
            OutlinedTextField(
                value = selectedVisibilidade,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = privacidadeExpanded,
                onDismissRequest = { privacidadeExpanded = false }
            ) {
                visibilidadeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedVisibilidade = option
                            privacidadeExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // ... (A tua lógica de criação de DTO fica igual)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Criar Evento")
            }
        }
    }
}
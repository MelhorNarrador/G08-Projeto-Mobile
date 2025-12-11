package pt.iade.lane.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import pt.iade.lane.components.EventFormState
import pt.iade.lane.components.EventVisualSection
import pt.iade.lane.components.toCreateEventDTO
import pt.iade.lane.components.toFormState
import pt.iade.lane.components.uriToBase64
import pt.iade.lane.components.validateCreateEventForm
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.theme.LaneTheme
import pt.iade.lane.ui.viewmodels.CreateEventViewModel
import java.math.BigDecimal
import java.util.Calendar
import pt.iade.lane.data.models.Evento

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
class CreateEventActivity : ComponentActivity() {

    private val eventoRepository = EventoRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getStringExtra("mode") ?: "create"
        val eventToEdit = intent.getParcelableExtra<Evento>("event")

        try {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                android.content.pm.PackageManager.GET_META_DATA
            )
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
                    factory = viewModelFactory {
                        initializer {
                            CreateEventViewModel(eventoRepository, sessionManager)
                        }
                    }
                )

                val filters by viewModel.filters.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val isSuccess by viewModel.isSuccess.collectAsState()
                val errorMessage by viewModel.errorMessage.collectAsState()

                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    viewModel.loadFilters()
                }
                LaunchedEffect(isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(
                            context,
                            if (mode == "edit") "Evento atualizado com sucesso!"
                            else "Evento criado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                LaunchedEffect(errorMessage) {
                    if (errorMessage != null) {
                        Toast.makeText(context, "Erro: $errorMessage", Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    if (mode == "edit") "Editar Evento"
                                    else "Criar Evento"
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Voltar"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    CreateEventScreen(
                        modifier = Modifier.padding(innerPadding),
                        isLoading = isLoading,
                        filterOptions = filters,
                        creatorId = viewModel.getCreatorId(),
                        mode = mode,
                        existingEvent = eventToEdit,
                        onSubmitClick = { eventDTO ->
                            if (mode == "edit" && eventToEdit != null) {
                                viewModel.updateEvent(eventToEdit.id, eventDTO)
                            } else {

                                viewModel.createEvent(eventDTO)
                            }
                        }
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
    mode: String,
    existingEvent: Evento?,
    onSubmitClick: (CreateEventDTO) -> Unit
) {
    val context = LocalContext.current

    var formState by remember {
        mutableStateOf(
            if (existingEvent != null) existingEvent.toFormState()
            else EventFormState()
        )
    }

    var tipoEventoExpanded by remember { mutableStateOf(false) }
    var privacidadeExpanded by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val visibilidadeOptions = listOf("public", "private")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    val placesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { intent ->
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    val texto = place.address ?: place.name ?: "Localização"
                    val latLng = place.latLng

                    formState = formState.copy(
                        localizacaoTexto = texto,
                        latitude = latLng?.let { BigDecimal.valueOf(it.latitude) } ?: BigDecimal.ZERO,
                        longitude = latLng?.let { BigDecimal.valueOf(it.longitude) } ?: BigDecimal.ZERO
                    )
                }
            }

            AutocompleteActivity.RESULT_ERROR -> {
                result.data?.let { intent ->
                    val status = Autocomplete.getStatusFromIntent(intent)
                    Toast.makeText(
                        context,
                        "Erro Google: ${status.statusMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    LaunchedEffect(existingEvent, filterOptions) {
        if (mode == "edit" && existingEvent != null && filterOptions.isNotEmpty()) {
            val matching = filterOptions.find { it.id == existingEvent.categoryId }
            if (matching != null) {
                formState = formState.copy(selectedFiltro = matching)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EventVisualSection(
            formState = formState,
            onFormChange = { formState = it },

            selectedImageUri = selectedImageUri,
            onCoverClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },

            filterOptions = filterOptions,
            tipoEventoExpanded = tipoEventoExpanded,
            onTipoEventoExpandedChange = { tipoEventoExpanded = it },

            privacidadeExpanded = privacidadeExpanded,
            onPrivacidadeExpandedChange = { privacidadeExpanded = it },
            visibilidadeOptions = visibilidadeOptions,
            onLocalizacaoClick = {
                try {
                    val fields = listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.LAT_LNG
                    )
                    val intent = Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY,
                        fields
                    ).build(context)
                    placesLauncher.launch(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Erro: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onDataClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val dataStr =
                            "$y-${(m + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
                        formState = formState.copy(data = dataStr)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            onHoraClick = {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, h, m ->
                        val horaStr =
                            "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                        formState = formState.copy(hora = horaStr)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val error = validateCreateEventForm(formState)
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val imagemString = selectedImageUri?.let { uri ->
                    uriToBase64(context, uri)
                } ?: existingEvent?.imageBase64

                val eventDTO = formState.toCreateEventDTO(
                    creatorId = creatorId ?: 0,
                    imagemBase64 = imagemString
                )

                onSubmitClick(eventDTO)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(
                    if (mode == "edit") "Guardar alterações"
                    else "Criar Evento"
                )
            }
        }
    }
}


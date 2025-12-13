package pt.iade.lane.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pt.iade.lane.components.BottomBar
import pt.iade.lane.components.EventCategoryColors
import pt.iade.lane.components.EventDetailsBottomSheet
import pt.iade.lane.components.ProfileBottomSheet
import pt.iade.lane.components.toUi
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.EventUi
import pt.iade.lane.data.utils.LocationUtils
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.theme.LaneTheme
import pt.iade.lane.ui.viewmodels.EventoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationUtils.requestLocationPermission(this)
        val sessionManager = SessionManager(applicationContext)
        setContent {
            LaneTheme {
                val eventoViewModel: EventoViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            EventoViewModel(sessionManager)
                        }
                    }
                )
                LaneApp(viewModel = eventoViewModel)
            }
        }
    }
}

@Composable
fun LaneApp(viewModel: EventoViewModel) {

    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val scope = rememberCoroutineScope()
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    var isProfileSheetOpen by remember { mutableStateOf(false) }
    val eventos by viewModel.eventos.collectAsState()
    val name = sessionManager.fetchUserName().orEmpty()
    val usernameRaw = sessionManager.fetchUserUsername().orEmpty()
    val username = if (usernameRaw.isNotEmpty()) "@$usernameRaw" else ""
    val bio = sessionManager.fetchUserBio().orEmpty()
    val profileImageBase64 = sessionManager.fetchUserProfileImage()
    val userId = sessionManager.fetchUserId()
    var joinedEventsVersion by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.refreshTrigger.collectLatest {
            joinedEventsVersion++
        }
    }
    val participatingEvents = remember(eventos, joinedEventsVersion) {
        val joinedIds = sessionManager.fetchJoinedEvents()
        eventos.filter { joinedIds.contains(it.id) }
    }
    val activeEvents =
        if (userId != null) eventos.filter {
            it.creatorId == userId
        }
        else
            emptyList()


    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(Intent(context, CreateEventActivity::class.java))
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Evento")
            }
        },
        bottomBar = {
            BottomBar(
                selectedIndex = selectedItemIndex,
                onItemSelected = { index ->
                    selectedItemIndex = index
                    when (index) {
                        0 -> {
                            Toast.makeText(
                                context,
                                "Ecrã de pesquisa ainda não implementado.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        1 -> {
                            isProfileSheetOpen = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MapContent(
                viewModel = viewModel,
                onJoinedEventsChanged = {
                    joinedEventsVersion++
                }
            )
        }

        ProfileBottomSheet(
            viewModel = viewModel,
            isOpen = isProfileSheetOpen,
            onDismiss = { isProfileSheetOpen = false }
        )
    }
}
@Composable
fun MapContent(
    viewModel: EventoViewModel,
    onJoinedEventsChanged: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("Map preview (GoogleMap não renderiza em @Preview)")
        }
        return
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val eventos by viewModel.eventos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var selectedEventUi by remember { mutableStateOf<EventUi?>(null) }
    var isSheetOpen by remember { mutableStateOf(false) }
    val sessionManager = remember(context) { SessionManager(context) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    val filtros by viewModel.filtros.collectAsState()
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(38.736946, -9.142685),
            5f
        )
    }
    val hasPermission = LocationUtils.hasLocationPermission(context)
    val eventosFiltrados = remember(eventos, selectedCategoryId) {
        if (selectedCategoryId == null) eventos
        else eventos.filter { it.categoryId == selectedCategoryId }
    }
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val loc = LocationUtils.getLastKnownLocation(context)
            if (loc != null) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(loc, 14f)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.carregarEventos()
                viewModel.carregarFiltros()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        isLoading -> {
            CircularProgressIndicator()
        }
        errorMessage != null -> {
            Text(text = "Erro: $errorMessage")
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = hasPermission
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false
                    )
                ) {
                    eventosFiltrados.forEach { evento ->
                        if (evento.latitude != null && evento.longitude != null) {
                            val lat = evento.latitude.toDouble()
                            val lng = evento.longitude.toDouble()

                            if (lat != 0.0 || lng != 0.0) {
                                val posicao = LatLng(lat, lng)
                                val markerHue =
                                    EventCategoryColors.hueForCategory(evento.categoryId)
                                Marker(
                                    state = MarkerState(position = posicao),
                                    title = evento.title,
                                    snippet = evento.location,
                                    icon = BitmapDescriptorFactory.defaultMarker(markerHue),
                                    onClick = {
                                        scope.launch {
                                            val count = viewModel.getParticipantsCount(evento.id)
                                            selectedEventUi = evento.toUi(
                                                currentParticipants = count,
                                                isUserJoined = false
                                            )
                                            isSheetOpen = true
                                        }
                                        true
                                    }
                                )
                            }
                        }
                    }
                }

                if (eventos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Não foram encontrados eventos.")
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .padding(bottom = 8.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = { showFilterMenu = !showFilterMenu }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filtrar eventos"
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos") },
                            onClick = {
                                selectedCategoryId = null
                                showFilterMenu = false
                            }
                        )
                        filtros.forEach { filtro ->
                            DropdownMenuItem(
                                text = { Text(filtro.nome ?: "Sem nome") },
                                onClick = {
                                    selectedCategoryId = filtro.id
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }

                    }
                if (isSheetOpen && selectedEventUi != null) {
                    EventDetailsBottomSheet(
                        event = selectedEventUi,
                        onDismissRequest = {
                            isSheetOpen = false
                            selectedEventUi = null
                        },
                        onParticipateClick = {
                            scope.launch {
                                val userId = sessionManager.fetchUserId()
                                if (userId == null) {
                                    Toast.makeText(
                                        context,
                                        "Utilizador não autenticado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                                val result =
                                    viewModel.joinEvent(selectedEventUi!!.id, userId)
                                val newCount =
                                    viewModel.getParticipantsCount(selectedEventUi!!.id)
                                when (result) {
                                    is EventoRepository.JoinResult.Success -> {
                                        sessionManager.addJoinedEvent(selectedEventUi!!.id)
                                        onJoinedEventsChanged()
                                        selectedEventUi = selectedEventUi?.copy(
                                            currentParticipants = newCount,
                                            isUserJoined = true
                                        )
                                        Toast.makeText(
                                            context,
                                            "Inscrição registada com sucesso.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    is EventoRepository.JoinResult.AlreadyJoined -> {
                                        sessionManager.addJoinedEvent(selectedEventUi!!.id)
                                        selectedEventUi = selectedEventUi?.copy(
                                            currentParticipants = newCount,
                                            isUserJoined = true
                                        )
                                        Toast.makeText(
                                            context,
                                            "Já estás inscrito neste evento.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    is EventoRepository.JoinResult.Error -> {
                                        selectedEventUi = selectedEventUi?.copy(
                                            currentParticipants = newCount
                                        )
                                        Toast.makeText(
                                            context,
                                            result.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }


@Preview(showBackground = true, showSystemUi = true, name = "MainActivity - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainActivityPreviewDark() {
    val context = LocalContext.current

    LaneTheme {
        LaneApp(
            viewModel = remember {
                EventoViewModel(SessionManager(context))
            }
        )
    }
}

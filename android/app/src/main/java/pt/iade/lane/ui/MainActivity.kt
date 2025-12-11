package pt.iade.lane.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import pt.iade.lane.components.BottomBar
import pt.iade.lane.components.EventDetailsBottomSheet
import pt.iade.lane.components.ProfileBottomSheet
import pt.iade.lane.components.toUi
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.EventUi
import pt.iade.lane.data.utils.LocationUtils
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.theme.LaneTheme
import pt.iade.lane.ui.viewmodels.EventoViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import pt.iade.lane.components.EventCategoryColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationUtils.requestLocationPermission(this)
        setContent {
            LaneTheme {
                val eventoViewModel: EventoViewModel = viewModel()
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
        floatingActionButtonPosition = FabPosition.Start,
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
            isOpen = isProfileSheetOpen,
            onDismiss = { isProfileSheetOpen = false },
            name = name,
            username = username,
            bio = bio,
            profileImageBase64 = profileImageBase64,
            activeEvents = activeEvents,
            participatingEvents = participatingEvents,
            onEditEventClick = { evento ->
                val intent = Intent(context, CreateEventActivity::class.java).apply {
                    putExtra("mode", "edit")
                    putExtra("event", evento)
                }
                context.startActivity(intent)
            },
            onDeleteEventClick = { evento ->
                scope.launch {
                    val ok = viewModel.deleteEvent(evento.id)
                    val msg = if (ok) "Evento apagado." else "Erro ao apagar evento."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            },
            onParticipatingEventLeaveClick = { evento ->
                val currentUserId = userId
                if (currentUserId == null) {
                    Toast.makeText(context, "Utilizador não autenticado.", Toast.LENGTH_SHORT).show()
                    return@ProfileBottomSheet
                }
                scope.launch {
                    when (val result = viewModel.leaveEvent(evento.id, currentUserId)) {
                        is EventoRepository.JoinResult.Success -> {
                            sessionManager.removeJoinedEvent(evento.id)
                            joinedEventsVersion++
                            viewModel.carregarEventos()
                            Toast.makeText(
                                context,
                                "Saíste do evento ${evento.title}.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is EventoRepository.JoinResult.Error -> {
                            Toast.makeText(
                                context,
                                result.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is EventoRepository.JoinResult.AlreadyJoined -> {
                            Toast.makeText(
                                context,
                                "Estado inconsistente ao sair do evento.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            onEditProfileClick = {},
            onChangePasswordClick = {},
            onLogoutClick = {
                sessionManager.clearAuth()
                (context as? Activity)?.let { activity ->
                    val intent = Intent(activity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    activity.startActivity(intent)
                    activity.finish()
                }
            }
        )
    }
}
@Composable
fun MapContent(
    viewModel: EventoViewModel,
    onJoinedEventsChanged: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val eventos by viewModel.eventos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var selectedEventUi by remember { mutableStateOf<EventUi?>(null) }
    var isSheetOpen by remember { mutableStateOf(false) }
    val sessionManager = remember(context) { SessionManager(context) }
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(38.736946, -9.142685),
            5f
        )
    }
    val hasPermission = LocationUtils.hasLocationPermission(context)
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
                        myLocationButtonEnabled = true
                    )
                ) {
                    eventos.forEach { evento ->
                        if (evento.latitude != null && evento.longitude != null) {
                            val lat = evento.latitude.toDouble()
                            val lng = evento.longitude.toDouble()

                            if (lat != 0.0 || lng != 0.0) {
                                val posicao = LatLng(lat, lng)
                                val markerHue = EventCategoryColors.hueForCategory(evento.categoryId)
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
}

@Preview(showBackground = true, showSystemUi = true, name = "LaneApp - Preview")
@Composable
fun LaneAppPreview() {
    LaneTheme {
        val previewViewModel = remember { EventoViewModel() }
        LaneApp(viewModel = previewViewModel)
    }
}

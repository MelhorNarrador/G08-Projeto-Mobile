package pt.iade.lane.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import pt.iade.lane.components.EventDetailsBottomSheet
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
        setContent {
            MaterialTheme {
                val eventoViewModel: EventoViewModel = viewModel()
                LaneApp(viewModel = eventoViewModel)
            }
        }
    }
}

@Composable
fun LaneApp(viewModel: EventoViewModel) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val items = listOf("Mapa", "Pesquisar", "Perfil")
    val context = LocalContext.current

    Scaffold(
        floatingActionButtonPosition = FabPosition.Start,
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            Log.d("Navigation", "Clicado: $item")
                        },
                        label = { Text(item) },
                        icon = {
                            when (item) {
                                "Mapa" -> Icon(Icons.Default.Map, contentDescription = "Mapa")
                                "Pesquisar" -> Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                                "Perfil" -> Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, CreateEventActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Criar Evento")
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MapContent(viewModel = viewModel)
        }
    }
}

@Composable
fun MapContent(viewModel: EventoViewModel) {
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
                        Log.d("BottomSheet", "imageBase64 length = ${evento.imageBase64?.length}")
                        if (evento.latitude != null && evento.longitude != null) {
                            val lat = evento.latitude.toDouble()
                            val lng = evento.longitude.toDouble()

                            if (lat != 0.0 || lng != 0.0) {
                                val posicao = LatLng(lat, lng)
                                Marker(
                                    state = MarkerState(position = posicao),
                                    title = evento.title,
                                    snippet = evento.location,
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

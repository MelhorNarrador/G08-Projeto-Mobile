package pt.iade.lane.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import kotlinx.coroutines.launch
import pt.iade.lane.components.EventCategoryColors
import pt.iade.lane.components.EventDetailsBottomSheet
import pt.iade.lane.components.toUi
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.EventUi
import pt.iade.lane.data.utils.LocationUtils
import pt.iade.lane.ui.viewmodels.EventoViewModel

@Composable
fun MapScreen(
    viewModel: EventoViewModel
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
    val lifecycleOwner = LocalLifecycleOwner.current

    val session by viewModel.session.collectAsState()

    val eventos by viewModel.eventos.collectAsState()
    val filtros by viewModel.filtros.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedEventUi by remember { mutableStateOf<EventUi?>(null) }
    var isSheetOpen by remember { mutableStateOf(false) }

    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(38.736946, -9.142685), 5f)
    }

    val hasPermission = LocationUtils.hasLocationPermission(context)

    val eventosFiltrados = remember(eventos, selectedCategoryId) {
        if (selectedCategoryId == null) eventos
        else eventos.filter { it.categoryId == selectedCategoryId }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val loc = LocationUtils.getLastKnownLocation(context)
            if (loc != null) cameraPositionState.position = CameraPosition.fromLatLngZoom(loc, 14f)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.carregarEventos()
                viewModel.carregarFiltros()
                viewModel.loadSession()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when {
        isLoading -> CircularProgressIndicator()
        errorMessage != null -> Text(text = "Erro: $errorMessage")
        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = hasPermission),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = false
                    )
                ) {
                    eventosFiltrados.forEach { evento ->
                        val lat = evento.latitude?.toDouble()
                        val lng = evento.longitude?.toDouble()
                        if (lat != null && lng != null && (lat != 0.0 || lng != 0.0)) {
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
                                        val joined = session.joinedEventIds.contains(evento.id)

                                        selectedEventUi = evento.toUi(
                                            currentParticipants = count,
                                            isUserJoined = joined
                                        )
                                        isSheetOpen = true
                                    }
                                    true
                                }
                            )
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
                    SmallFloatingActionButton(onClick = { showFilterMenu = !showFilterMenu }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filtrar eventos")
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

                if (isSheetOpen && selectedEventUi != null) {
                    EventDetailsBottomSheet(
                        event = selectedEventUi,
                        onDismissRequest = {
                            isSheetOpen = false
                            selectedEventUi = null
                        },
                        onParticipateClick = {
                            scope.launch {
                                val userId = session.userId
                                if (userId == null) {
                                    Toast.makeText(
                                        context,
                                        "Utilizador não autenticado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }

                                val eventId = selectedEventUi!!.id
                                val result = viewModel.joinEvent(eventId, userId)
                                val newCount = viewModel.getParticipantsCount(eventId)

                                when (result) {
                                    is EventoRepository.JoinResult.Success -> {
                                        viewModel.markJoined(eventId)
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
                                        viewModel.markJoined(eventId)
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
                                        selectedEventUi =
                                            selectedEventUi?.copy(currentParticipants = newCount)
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

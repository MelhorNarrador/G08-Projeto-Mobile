package pt.iade.lane.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.ui.viewmodels.EventoViewModel

class MainActivity : ComponentActivity() {

    private val eventoRepository = EventoRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val eventoViewModel: EventoViewModel = viewModel(
                    factory = EventoViewModel.Factory(eventoRepository)
                )
                LaneApp(viewModel = eventoViewModel)
            }
        }
    }
}

@Composable
fun LaneApp(viewModel: EventoViewModel) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val items = listOf("Mapa", "Pesquisar", "Perfil")

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            // No futuro, aqui navegaríamos para outros ecrãs
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
        }
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

    val eventos by viewModel.eventos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarEventos()
    }

    when {
        isLoading -> {
            CircularProgressIndicator()
        }

        errorMessage != null -> {
            Text(text = "Erro: $errorMessage")
        }

        else -> {
            GoogleMap(
                modifier = Modifier.fillMaxSize()
            ) {
                eventos.forEach { evento ->
                    if (evento.latitude != null && evento.longitude != null) {
                        val posicao = LatLng(evento.latitude, evento.longitude)
                        Marker(
                            state = MarkerState(position = posicao),
                            title = evento.titulo,
                            snippet = evento.localizacao
                        )
                    }
                }
            }

            if (eventos.isEmpty() && !isLoading) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Não foram encontrados eventos.")
                }
            }
        }
    }
}
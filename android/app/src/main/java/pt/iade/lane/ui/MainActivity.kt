package pt.iade.lane.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.iade.lane.data.utils.LocationUtils
import pt.iade.lane.ui.theme.LaneTheme
import pt.iade.lane.ui.viewmodels.EventoViewModel
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import pt.iade.lane.ui.theme.LaneTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationUtils.requestLocationPermission(this)

        setContent {
            LaneTheme {
                val eventoViewModel: EventoViewModel = viewModel()
                HomeRoute(viewModel = eventoViewModel)
            }
        }
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Main - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainPreviewDark() {
    LaneTheme {
        val vm = remember { EventoViewModel() }
        HomeRoute(viewModel = vm)
    }
}

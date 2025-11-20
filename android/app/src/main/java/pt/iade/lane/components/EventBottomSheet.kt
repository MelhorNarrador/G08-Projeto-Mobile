package pt.iade.lane.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.utils.EventUi
import androidx.compose.ui.graphics.asImageBitmap
import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import pt.iade.lane.components.decodeBase64ToBitmapSafe
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.heightIn



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsBottomSheet(
    event: EventUi?,
    onDismissRequest: () -> Unit,
    onParticipateClick: () -> Unit
) {
    if (event == null) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismissRequest()
            }
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            if (!event.imageBase64.isNullOrBlank()) {
                val bitmap = remember(event.imageBase64) {
                    decodeBase64ToBitmapSafe(event.imageBase64)
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imagem do evento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.location,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp) // altura máxima visível
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NavigationModeButton(
                        label = "A pé",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            openGoogleMapsNavigation(
                                context = context,
                                lat = event.latitude,
                                lng = event.longitude,
                                mode = "w"
                            )
                        }
                    )

                    NavigationModeButton(
                        label = "De carro",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            openGoogleMapsNavigation(
                                context = context,
                                lat = event.latitude,
                                lng = event.longitude,
                                mode = "d"
                            )
                        }
                    )

                    NavigationModeButton(
                        label = "Publico",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            openGoogleMapsNavigation(
                                context = context,
                                lat = event.latitude,
                                lng = event.longitude,
                                mode = "r"
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                val isFull = event.currentParticipants >= event.maxParticipants
                val buttonText = if (isFull) {
                    "Esgotado"
                } else {
                    "Participar (${event.currentParticipants}/${event.maxParticipants})"
                }

                Button(
                    onClick = {
                        if (!isFull) {
                            onParticipateClick()
                        }
                    },
                    enabled = !isFull,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFull) Color.Red else MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Red,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = buttonText)
                }
            }
        }
    }
}

@Composable
private fun NavigationModeButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

private fun openGoogleMapsNavigation(
    context: Context,
    lat: Double,
    lng: Double,
    mode: String
) {
    val uri = Uri.parse("google.navigation:q=$lat,$lng&mode=$mode")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=${
            when (mode) {
                "w" -> "walking"
                "d" -> "driving"
                "r" -> "transit"
                else -> "driving"
            }
        }")
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}

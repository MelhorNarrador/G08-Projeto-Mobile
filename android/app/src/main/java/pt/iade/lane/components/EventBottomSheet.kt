package pt.iade.lane.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import pt.iade.lane.data.utils.EventUi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatEventDateTime(raw: String): String {
    return try {
        val ldt = LocalDateTime.parse(raw)
        val formatter = DateTimeFormatter.ofPattern("'Data:'dd/MM/yy 'Hora:'HH:mm")
        ldt.format(formatter)
    } catch (_: Exception) {
        raw.replace("T", " ")
    }
}
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

                val formattedDateTime = remember(event.dateTime) {
                    formatEventDateTime(event.dateTime)
                }

                Text(
                    text = formattedDateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
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
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        contentDescription = "A pé",
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
                        icon = Icons.Filled.DirectionsCar,
                        contentDescription = "De carro",
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
                        icon = Icons.Filled.Train,
                        contentDescription = "Transportes",
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
                val isJoined = event.isUserJoined
                val buttonText = when {
                    isFull -> "Esgotado"
                    isJoined -> "Já inscrito"
                    else -> "Participar (${event.currentParticipants}/${event.maxParticipants})"
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
                        containerColor = when {
                            isFull -> Color.Red
                            else -> MaterialTheme.colorScheme.primary
                        },
                        disabledContainerColor = when {
                            isFull -> Color.Red
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        },
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
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

private fun openGoogleMapsNavigation(
    context: Context,
    lat: Double,
    lng: Double,
    mode: String
) {
    val uri = "google.navigation:q=$lat,$lng&mode=$mode".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val webUri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=${
            when (mode) {
                "w" -> "walking"
                "d" -> "driving"
                "r" -> "transit"
                else -> "driving"
            }
        }".toUri()
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}
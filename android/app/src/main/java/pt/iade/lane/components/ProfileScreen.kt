package pt.iade.lane.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.utils.EventUi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
@Composable
fun ProfileScreenContent(
    name: String,
    username: String,
    bio: String,
    profileImageBase64: String?,
    activeEvents: List<Evento>,
    participatingEvents: List<Evento>,
    onEditEvent: (Evento) -> Unit,
    onDeleteEvent: (Evento) -> Unit,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onParticipatingEventLeave: (Evento) -> Unit,
    onLogout: () -> Unit
) {
    var showActive by remember { mutableStateOf(false) }
    var showParticipating by remember { mutableStateOf(false) }
    var selectedParticipatingEvent by remember { mutableStateOf<EventUi?>(null) }
    var isDetailsSheetOpen by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // evita ficar cortado pelos gestos/bottom bar
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            ProfileHeader(
                name = name,
                username = username,
                imageBase64 = profileImageBase64
            )
        }

        if (bio.isNotBlank()) {
            item {
                Text(text = bio, style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            Text(
                text = "Estatísticas",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            StatItem(
                label = "Eventos ativos",
                count = activeEvents.size,
                expanded = showActive,
                onClick = { showActive = !showActive }
            )
        }

        if (showActive) {
            if (activeEvents.isEmpty()) {
                item {
                    Text(
                        text = "Sem eventos ativos.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(activeEvents, key = { it.id }) { evento ->
                    ActiveEventRow(
                        evento = evento,
                        onEditEvent = onEditEvent,
                        onDeleteEvent = onDeleteEvent
                    )
                }
            }
        }

        item {
            StatItem(
                label = "Eventos a participar",
                count = participatingEvents.size,
                expanded = showParticipating,
                onClick = { showParticipating = !showParticipating }
            )
        }

        if (showParticipating) {
            if (participatingEvents.isEmpty()) {
                item {
                    Text(
                        text = "Sem eventos a participar.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(participatingEvents, key = { it.id }) { evento ->
                    ParticipatingEventRow(
                        evento = evento,
                        onInfoClick = {
                            selectedParticipatingEvent = evento.toUi(isUserJoined = true)
                            isDetailsSheetOpen = true
                        },
                        onLeaveClick = { onParticipatingEventLeave(evento) }
                    )
                }
            }
        }

        item {
            Text(
                text = "Definições",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Terminar sessão")
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    if (isDetailsSheetOpen && selectedParticipatingEvent != null) {
        EventDetailsBottomSheet(
            event = selectedParticipatingEvent,
            onDismissRequest = {
                isDetailsSheetOpen = false
                selectedParticipatingEvent = null
            },
            onParticipateClick = {}
        )
    }
}


@Composable
private fun ProfileHeader(
    name: String,
    username: String,
    imageBase64: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!imageBase64.isNullOrEmpty()) {
            val dataUri = "data:image/jpeg;base64,$imageBase64"
            Image(
                painter = rememberAsyncImagePainter(dataUri),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray, CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3A82F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (name.firstOrNull() ?: '?').uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (name.isNotBlank()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (username.isNotBlank()) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    count: Int,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ActiveEventRow(
    evento: Evento,
    onEditEvent: (Evento) -> Unit,
    onDeleteEvent: (Evento) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = evento.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatEventDateTime(evento.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = { onEditEvent(evento) }) {
                    Text("Editar")
                }
                TextButton(onClick = { onDeleteEvent(evento) }) {
                    Text("Apagar")
                }
            }
        }
    }
}

@Composable
private fun ParticipatingEventRow(
    evento: Evento,
    onInfoClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = evento.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formatEventDateTime(evento.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onInfoClick) {
                    Text("Info")
                }
                TextButton(onClick = onLeaveClick) {
                    Text("Sair")
                }
            }
        }
    }
}
@Composable
private fun ProfileOptionItem(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

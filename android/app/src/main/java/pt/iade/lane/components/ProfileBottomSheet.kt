package pt.iade.lane.components

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.CreateEventActivity
import pt.iade.lane.ui.LoginActivity
import pt.iade.lane.ui.viewmodels.EventoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    viewModel: EventoViewModel,
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val eventos by viewModel.eventos.collectAsState()
    val userId = sessionManager.fetchUserId()

    var joinedIds by remember { mutableStateOf(sessionManager.fetchJoinedEvents()) }
    LaunchedEffect(Unit) {
        viewModel.refreshTrigger.collect {
            joinedIds = sessionManager.fetchJoinedEvents()
        }
    }
    val activeEvents = eventos.filter { it.creatorId == userId }
    val participatingEvents = eventos.filter { joinedIds.contains(it.id) }
    val name = sessionManager.fetchUserName().orEmpty()
    val username = sessionManager.fetchUserUsername().let { if (!it.isNullOrEmpty()) "@$it" else "" }
    val bio = sessionManager.fetchUserBio().orEmpty()
    val profileImageBase64 = sessionManager.fetchUserProfileImage()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        ProfileScreenContent(
            name = name,
            username = username,
            bio = bio,
            profileImageBase64 = profileImageBase64,
            activeEvents = activeEvents,
            participatingEvents = participatingEvents,

            onEditEvent = { evento ->
                val intent = Intent(context, CreateEventActivity::class.java).apply {
                    putExtra("mode", "edit")
                    putExtra("eventId", evento.id)
                }
                context.startActivity(intent)
            },

            onDeleteEvent = { evento ->
                viewModel.deleteEvent(evento.id)
            },
            onParticipatingEventLeave = { evento ->
                viewModel.leaveEvent(evento.id)
            },

            onLogout = {
                sessionManager.clearAuth()
                (context as? Activity)?.let { activity ->
                    val intent = Intent(activity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    activity.startActivity(intent)
                    activity.finish()
                }
            },

            onEditProfile = {},
            onChangePassword = {}
        )
    }
}
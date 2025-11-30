package pt.iade.lane.components

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.Evento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    name: String,
    username: String,
    bio: String,
    profileImageBase64: String?,
    activeEvents: List<Evento>,
    participatingEvents: List<Evento>,
    onEditEventClick: (Evento) -> Unit,
    onDeleteEventClick: (Evento) -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

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
            onEditEvent = onEditEventClick,
            onDeleteEvent = onDeleteEventClick,
            onEditProfile = onEditProfileClick,
            onChangePassword = onChangePasswordClick,
            onLogout = onLogoutClick
        )
    }
}
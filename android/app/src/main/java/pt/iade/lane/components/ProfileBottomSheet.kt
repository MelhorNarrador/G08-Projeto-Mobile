package pt.iade.lane.components

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    name: String,
    username: String,
    bio: String,
    profileImageBase64: String?,
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
            onEditProfile = onEditProfileClick,
            onChangePassword = onChangePasswordClick,
            onLogout = onLogoutClick
        )
    }
}

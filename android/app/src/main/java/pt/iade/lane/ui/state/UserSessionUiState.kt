package pt.iade.lane.ui.state

data class UserSessionUiState(
    val userId: Int? = null,
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val profileImageBase64: String? = null,
    val joinedEventIds: Set<Int> = emptySet(),
    val isLoggedIn: Boolean = false
)

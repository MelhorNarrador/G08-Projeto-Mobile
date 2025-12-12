package pt.iade.lane.data.utils

data class EventUi(
    val id: Int,
    val title: String,
    val description: String,
    val imageBase64: String?,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val dateTime: String,
    val currentParticipants: Int,
    val maxParticipants: Int,
    val isUserJoined: Boolean = false,
    val price: Double
)
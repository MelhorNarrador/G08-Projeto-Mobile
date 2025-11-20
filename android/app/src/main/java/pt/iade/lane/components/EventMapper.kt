package pt.iade.lane.components

import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.utils.EventUi

fun Evento.toUi(
    currentParticipants: Int = 0,
    formattedDateTime: String = this.date
): EventUi {
    return EventUi(
        id = id,
        title = title,
        description = description ?: "",
        imageBase64 = imageBase64,
        location = location ?: "",
        latitude = latitude?.toDouble() ?: 0.0,
        longitude = longitude?.toDouble() ?: 0.0,
        dateTime = formattedDateTime,
        currentParticipants = currentParticipants,
        maxParticipants = maxParticipants
    )
}

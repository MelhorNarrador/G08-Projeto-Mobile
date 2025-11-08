package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName

data class EventParticipants(

    @SerializedName("participant_id")
    val id: Int,

    @SerializedName("event_id")
    val eventoId: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("joined_at")
    val joinedAt: String
)
package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Convite(
    @SerializedName("invitations_id")
    val id: Int,

    @SerializedName("event_id")
    val eventoId: Int,

    @SerializedName("sender_id")
    val senderId: Int,

    @SerializedName("receiver_id")
    val receiverId: Int,

    @SerializedName("status")
    val status: String, // "pending", "accepted", "rejected"

    @SerializedName("sent_at")
    val enviadoEm: Date
)
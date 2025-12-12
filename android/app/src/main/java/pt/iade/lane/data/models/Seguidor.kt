package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Seguidor(
    @SerializedName("follow_id")
    val id: Int,

    @SerializedName("follower_id")
    val seguidorId: Int,

    @SerializedName("following_id")
    val aSerSeguidoId: Int,

    @SerializedName("followed_at")
    val seguidoEm: Date
)
package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Amigo(
    @SerializedName("friends_id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int, // O utilizador que iniciou a ligação

    @SerializedName("friends_user_id")
    val amigoUserId: Int, // O utilizador que é o amigo

    @SerializedName("created_at")
    val criadoEm: Date
)
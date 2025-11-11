package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponseDTO(
    @SerializedName("token")
    val token: String,

    @SerializedName("userId")
    val userId: Int
)
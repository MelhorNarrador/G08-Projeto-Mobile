package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponseDTO(
    @SerializedName("token")
    val token: String,

    @SerializedName(value = "userId", alternate = ["userid", "account_id", "id", "accountId"])
    val userId: Int
)
package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName

data class LoginRequestDTO(
    @SerializedName("account_email")
    val email: String,

    @SerializedName("password")
    val passwordPura: String
)
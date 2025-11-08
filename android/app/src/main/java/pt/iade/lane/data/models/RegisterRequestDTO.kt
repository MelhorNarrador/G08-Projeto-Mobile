package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName

data class RegisterRequestDTO(

    @SerializedName("account_name")
    val nome: String,

    @SerializedName("account_username")
    val username: String,

    @SerializedName("account_email")
    val email: String,

    @SerializedName("password")
    val passwordPura: String,

    @SerializedName("account_dob")
    val dataNascimento: String,

    @SerializedName("account_gender")
    val genero: String
)
package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName

data class Utilizador(

    @SerializedName("account_id")
    val id: Int,

    @SerializedName("account_name")
    val nome: String,

    @SerializedName("account_username")
    val username: String,

    @SerializedName("account_email")
    val email: String,

    @SerializedName("account_bio")
    val bio: String?,

    @SerializedName("account_photo_url")
    val fotoUrl: String?,

    @SerializedName("account_verified")
    val isVerificado: Boolean
)
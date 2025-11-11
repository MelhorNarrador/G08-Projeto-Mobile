package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CreateEventDTO(

    @SerializedName("event_title")
    val titulo: String,

    @SerializedName("event_description")
    val descricao: String?,

    @SerializedName("event_visibility")
    val visibilidade: String,

    @SerializedName("event_category_id")
    val categoriaId: Int,

    @SerializedName("event_creator_id")
    val criadorId: Int,

    @SerializedName("location")
    val localizacao: String,

    @SerializedName("event_latitude")
    val latitude: BigDecimal,

    @SerializedName("event_longitude")
    val longitude: BigDecimal,

    @SerializedName("event_date")
    val data: String,

    @SerializedName("event_price")
    val preco: BigDecimal,

    @SerializedName("max_participants")
    val maxParticipantes: Int
)
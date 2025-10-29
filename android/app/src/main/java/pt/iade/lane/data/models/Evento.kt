package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date // O Gson pode precisar de ajuda para converter datas


data class Evento(

    @SerializedName("event_id")
    val id: Int,

    @SerializedName("event_title")
    val titulo: String,

    @SerializedName("event_description")
    val descricao: String?,

    @SerializedName("event_visibility")
    val visibilidade: String, // "public", "private", "invite"

    @SerializedName("event_category_id")
    val categoriaId: Int?,

    @SerializedName("event_creator_id")
    val criadorId: Int,

    @SerializedName("location")
    val localizacao: String?,

    @SerializedName("event_latitude")
    val latitude: Double?,

    @SerializedName("event_longitude")
    val longitude: Double?,

    @SerializedName("event_date")
    val data: Date, // Se a API enviar como String (ex: "2025-10-23T17:17:51")
    // o Gson vai tentar converter. Pode ser preciso um 'TypeAdapter'
    // mas por agora, vamos assumir que funciona.

    @SerializedName("event_price")
    val preco: Double, // Na BD está 'DEFAULT 0', por isso não deve ser nulo (Talvez Corriga)

    @SerializedName("created_at")
    val criadoEm: Date
)
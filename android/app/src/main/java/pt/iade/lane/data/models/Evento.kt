package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Evento(
    @SerializedName("event_id")
    val id: Int,

    @SerializedName("event_title")
    val title: String,

    @SerializedName("event_description")
    val description: String?,

    @SerializedName("event_visibility")
    val visibility: String,

    @SerializedName("event_category_id")
    val categoryId: Int,

    @SerializedName("event_creator_id")
    val creatorId: Int,

    @SerializedName("location")
    val location: String?,

    @SerializedName("event_latitude")
    val latitude: BigDecimal?,

    @SerializedName("event_longitude")
    val longitude: BigDecimal?,

    @SerializedName("event_date")
    val date: String,

    @SerializedName("event_price")
    val price: BigDecimal,

    @SerializedName("max_participants")
    val maxParticipants: Int
)
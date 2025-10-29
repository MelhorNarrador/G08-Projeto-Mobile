package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName

data class Filtro(
    @SerializedName("filters_id")
    val id: Int,

    @SerializedName("filters_name")
    val nome: String
)
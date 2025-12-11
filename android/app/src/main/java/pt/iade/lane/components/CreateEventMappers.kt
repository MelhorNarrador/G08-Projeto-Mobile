package pt.iade.lane.components

import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro
import java.math.BigDecimal
import pt.iade.lane.data.models.Evento


data class EventFormState(
    val titulo: String = "",
    val descricao: String = "",
    val preco: String = "0",
    val maxParticipantes: String = "100",
    val localizacaoTexto: String = "",
    val latitude: BigDecimal = BigDecimal.ZERO,
    val longitude: BigDecimal = BigDecimal.ZERO,
    val selectedFiltro: Filtro? = null,
    val selectedVisibilidade: String = "public",
    val data: String = "",
    val hora: String = ""
)

fun Evento.toFormState(): EventFormState {
    // Exemplo típico: "2025-01-10T21:00:00"
    val (dataPart, timePartRaw) = if (date.contains("T")) {
        val parts = date.split("T")
        val data = parts.getOrNull(0) ?: ""
        val hora = parts.getOrNull(1) ?: ""
        data to hora
    } else {
        // fallback se o backend algum dia mandar sem 'T'
        date to ""
    }

    // timePartRaw: "21:00:00" -> queremos só "21:00"
    val horaPart = if (timePartRaw.length >= 5) {
        timePartRaw.substring(0, 5)
    } else {
        ""
    }

    return EventFormState(
        titulo = title,
        descricao = description ?: "",
        preco = price.toPlainString(),
        maxParticipantes = maxParticipants.toString(),
        localizacaoTexto = location ?: "",
        latitude = latitude ?: BigDecimal.ZERO,
        longitude = longitude ?: BigDecimal.ZERO,
        selectedFiltro = null,
        selectedVisibilidade = visibility,
        data = dataPart,
        hora = horaPart
    )
}

fun validateCreateEventForm(state: EventFormState): String? {
    if (state.titulo.isBlank()) return "O título é obrigatório"
    if (state.data.isBlank()) return "A data é obrigatória"
    if (state.hora.isBlank()) return "A hora é obrigatória"
    if (state.localizacaoTexto.isBlank()) return "A localização é obrigatória"
    if (state.selectedFiltro == null) return "A categoria é obrigatória"
    return null
}
fun EventFormState.toCreateEventDTO(
    creatorId: Int,
    imagemBase64: String?
): CreateEventDTO {
    val precoBigDecimal = try {
        BigDecimal(preco)
    } catch (_: Exception) {
        BigDecimal.ZERO
    }

    val maxPartInt = try {
        maxParticipantes.toInt()
    } catch (_: Exception) {
        0
    }

    val dataFinal = "${data}T${hora}:00"

    return CreateEventDTO(
        titulo = titulo,
        descricao = descricao,
        visibilidade = selectedVisibilidade,
        categoriaId = selectedFiltro!!.id,
        criadorId = creatorId,
        localizacao = localizacaoTexto,
        latitude = latitude,
        longitude = longitude,
        data = dataFinal,
        preco = precoBigDecimal,
        maxParticipantes = maxPartInt,
        imagemBase64 = imagemBase64,
        id = 0,
        name = ""
    )
}
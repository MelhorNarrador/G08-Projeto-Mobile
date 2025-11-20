package pt.iade.lane.components

import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro
import java.math.BigDecimal

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
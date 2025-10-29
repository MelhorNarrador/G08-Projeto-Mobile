package pt.iade.lane.data.repository

import android.util.Log
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.network.RetrofitClient

/**
 * Repositório para gerir todos os dados relacionados com 'Eventos'.
 */
class EventoRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Vai buscar todos os eventos à API.
     * Devolve uma lista vazia em caso de erro.
     */
    suspend fun getTodosEventos(): List<Evento> {
        return try {
            apiService.getTodosEventos()
        } catch (e: Exception) {
            Log.e("EventoRepository", "Falha ao buscar eventos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Vai buscar um evento específico pelo seu ID.
     * Devolve 'null' em caso de erro.
     */
    suspend fun getEventoPorId(eventId: Int): Evento? {
        return try {
            apiService.getEventoPorId(eventId)
        } catch (e: Exception) {
            Log.e("EventoRepository", "Falha ao buscar evento $eventId: ${e.message}")
            null
        }
    }

    /**
     * Tenta criar um novo evento na API.
     * Devolve o evento criado (com o ID dado pela BD) ou 'null' se falhar.
     */
    suspend fun criarEvento(novoEvento: Evento): Evento? {
        return try {
            val response = apiService.criarEvento(novoEvento)
            if (response.isSuccessful) {
                response.body() // Devolve o evento que a API retornou
            } else {
                Log.e("EventoRepository", "Falha ao criar evento: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("EventoRepository", "Exceção ao criar evento: ${e.message}")
            null
        }
    }
}
package pt.iade.lane.data.repository

import android.util.Log
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.network.RetrofitClient

class EventoRepository {

    private val apiService = RetrofitClient.apiService
    suspend fun getTodosEventos(): List<Evento> {
        return try {
            apiService.getTodosEventos()
        } catch (e: Exception) {
            Log.e("EventoRepository", "Falha ao buscar eventos: ${e.message}")
            emptyList()
        }
    }
    suspend fun criarEvento(novoEvento: Evento): Evento? {
        return try {
            val response = apiService.criarEvento(novoEvento)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("EventoRepository", "Falha ao criar evento: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("EventoRepository", "Exceção ao criar evento: ${e.message}")
            null
        }
    }
}
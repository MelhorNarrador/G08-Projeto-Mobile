package pt.iade.lane.data.repository

import android.util.Log
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.network.RetrofitClient
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro
import retrofit2.Response

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
    suspend fun criarEvento(request: CreateEventDTO): Evento? {
        return try {
            val response = apiService.criarEvento(request)
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
    suspend fun getFiltros(): List<Filtro> {
        return try {
            val response = apiService.getFiltros()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("EventoRepository", "Falha ao buscar filtros: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("EventoRepository", "Exceção ao buscar filtros: ${e.message}")
            emptyList()
        }
    }
}
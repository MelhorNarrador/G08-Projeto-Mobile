package pt.iade.lane.data.repository

import android.util.Log
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.models.Filtro
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
    suspend fun getParticipantsCount(eventId: Int): Int {
        return try {
            apiService.getParticipantsCount(eventId).toInt()
        } catch (e: Exception) {
            Log.e("EventoRepository", "Falha ao buscar participantes: ${e.message}")
            0
        }
    }
    sealed class JoinResult {
        object Success : JoinResult()
        object AlreadyJoined : JoinResult()
        data class Error(val message: String) : JoinResult()
    }
    suspend fun joinEvent(eventId: Int, userId: Int): JoinResult {
        return try {
            val response = apiService.joinEvent(eventId, userId)
            val code = response.code()
            val msg = response.message()
            val errorBody = response.errorBody()?.string()
            android.util.Log.d(
                "EventoRepository", "joinEvent: code=$code msg=$msg errorBody=$errorBody")
            when {
                response.isSuccessful -> JoinResult.Success
                code == 409 -> JoinResult.AlreadyJoined
                else -> JoinResult.Error("Erro ao participar (HTTP $code)")
            }
        }
        catch(e: Exception) {
                Log.e("EventoRepository", "Exceção ao participar: ${e.message}")
                JoinResult.Error("Erro de rede ao participar no evento.")
            }
    }
}
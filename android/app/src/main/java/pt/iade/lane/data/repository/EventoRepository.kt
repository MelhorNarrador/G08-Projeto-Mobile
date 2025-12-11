package pt.iade.lane.data.repository

import android.util.Log
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.network.RetrofitClient
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro

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
                Log.e(
                    "EventoRepository",
                    "Falha ao criar evento: ${response.code()} ${response.message()}"
                )
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

    suspend fun deleteEvento(id: Int): Boolean {
        return try {
            val response = apiService.deleteEvent(id)
            if (!response.isSuccessful) {
                Log.e("EventoRepository", "Falha ao apagar evento: ${response.code()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("EventoRepository", "Exceção ao apagar evento: ${e.message}")
            false
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
            Log.d(
                "EventoRepository",
                "joinEvent code=${response.code()} body=${response.errorBody()?.string()}"
            )

            when {
                response.isSuccessful -> JoinResult.Success
                response.code() == 409 -> JoinResult.AlreadyJoined
                response.code() == 403 -> JoinResult.Error("Acesso negado (403).")
                else -> JoinResult.Error("Erro ao participar: ${response.code()}")
            }
        } catch (e: Exception) {
            JoinResult.Error("Erro de rede: ${e.message}")
        }
    }
    suspend fun leaveEvent(eventId: Int, userId: Int): JoinResult {
        return try {
            val response = apiService.leaveEvent(eventId, userId)

            if (response.isSuccessful) {
                JoinResult.Success
            } else {
                JoinResult.Error("Erro ao sair do evento: ${response.code()}")
            }
        } catch (e: Exception) {
            JoinResult.Error("Erro de rede ao sair: ${e.message}")
        }
    }


    suspend fun getParticipantsCount(eventId: Int): Int {
        return try {
            apiService.getParticipantsCount(eventId).toInt()
        } catch (e: Exception) {
            Log.e("EventoRepository", "Erro ao buscar participantes: ${e.message}")
            0
        }
    }
    suspend fun updateEvento(eventId: Int, dto: CreateEventDTO): Boolean {
        return try {
            val response = apiService.updateEvent(eventId, dto)

            if (!response.isSuccessful) {
                Log.e(
                    "EventoRepository",
                    "Erro updateEvento: code=${response.code()} body=${response.errorBody()?.string()}"
                )
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("EventoRepository", "Exceção em updateEvento", e)
            false
        }
    }
}


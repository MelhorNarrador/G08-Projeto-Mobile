package pt.iade.lane.data.repository

import android.util.Log
import pt.iade.lane.data.models.RegisterRequestDTO
import pt.iade.lane.data.models.Utilizador
import pt.iade.lane.data.network.RetrofitClient

class UtilizadorRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getTodosUtilizadores(): List<Utilizador> {
        return try {
            apiService.getTodosUtilizadores()
        } catch (e: Exception) {
            Log.e("UtilizadorRepository", "Falha ao buscar utilizadores: ${e.message}")
            emptyList()
        }
    }
    suspend fun registarUtilizador(request: RegisterRequestDTO): Utilizador? {
        return try {
            val response = apiService.registarUtilizador(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UtilizadorRepository", "Falha ao registar: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UtilizadorRepository", "Exceção ao registar: ${e.message}", e)
            null
        }
    }
}
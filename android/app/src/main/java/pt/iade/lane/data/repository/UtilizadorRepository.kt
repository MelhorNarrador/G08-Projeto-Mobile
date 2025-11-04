package pt.iade.lane.data.repository

import android.util.Log // Importa o Log para vermos os erros
import pt.iade.lane.data.models.Utilizador
import pt.iade.lane.data.network.RetrofitClient

/**
 * Repositório para gerir todos os dados relacionados com 'Utilizadores'.
 * API (RetrofitClient) e ViewModels.
 */
class UtilizadorRepository {
    private val apiService = RetrofitClient.apiService

    /**
     * Vai buscar todos os utilizadores à API.
     * Devolve uma lista vazia em caso de erro.
     */
    suspend fun getTodosUtilizadores(): List<Utilizador> {
        return try {
            // Tenta fazer a chamada de rede
            apiService.getTodosUtilizadores()
        } catch (e: Exception) {
            // Se falhar (sem net, API offline, etc.), apanha o erro
            Log.e("UtilizadorRepository", "Falha ao buscar utilizadores: ${e.message}")
            emptyList() // Devolve uma lista vazia para a app não crashar
        }
    }

    /**
     * Vai buscar um utilizador específico pelo seu ID.
     * Devolve 'null' em caso de erro ou se não for encontrado.
     */
    suspend fun getUtilizadorPorId(userId: Int): Utilizador? {
        return try {
            apiService.getUtilizadorPorId(userId)
        } catch (e: Exception) {
            Log.e("UtilizadorRepository", "Falha ao buscar utilizador $userId: ${e.message}")
            null // Devolve null em caso de erro
        }
    }

    /**
     * Vai buscar a lista de amigos de um utilizador.
     */
    suspend fun getAmigosDoUtilizador(userId: Int): List<Utilizador> {
        return try {
            apiService.getAmigosDoUtilizador(userId)
        } catch (e: Exception) {
            Log.e("UtilizadorRepository", "Falha ao buscar amigos de $userId: ${e.message}")
            emptyList()
        }
    }

    /**
     * Vai buscar a lista de seguidores de um utilizador.
     */
    suspend fun getSeguidores(userId: Int): List<Utilizador> {
        return try {
            apiService.getSeguidores(userId)
        } catch (e: Exception) {
            Log.e("UtilizadorRepository", "Falha ao buscar seguidores de $userId: ${e.message}")
            emptyList()
        }
    }
}
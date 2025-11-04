package pt.iade.lane.data.network

import pt.iade.lane.data.models.Amigo
import pt.iade.lane.data.models.Convite
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.models.Seguidor
import pt.iade.lane.data.models.Utilizador

// Importa anotações do Retrofit
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface que define todos os endpoints da API Lane.
 */
interface LaneAppAPIService {
    @GET("users")
    suspend fun getTodosUtilizadores(): List<Utilizador>

    @GET("users/{id}")
    suspend fun getUtilizadorPorId(@Path("id") userId: Int): Utilizador

    @PUT("users/{id}")
    suspend fun atualizarUtilizador(@Path("id") userId: Int, @Body utilizador: Utilizador): Response<Utilizador>

    @GET("events")
    suspend fun getTodosEventos(): List<Evento>

    @GET("events")
    suspend fun getEventosPorCategoria(@Query("category") categoriaId: Int): List<Evento>

    @GET("events/{id}")
    suspend fun getEventoPorId(@Path("id") eventId: Int): Evento

    @POST("events")
    suspend fun criarEvento(@Body novoEvento: Evento): Response<Evento>

    @PUT("events/{id}")
    suspend fun atualizarEvento(@Path("id") eventId: Int, @Body evento: Evento): Response<Evento>

    @DELETE("events/{id}")
    suspend fun apagarEvento(@Path("id") eventId: Int): Response<Unit>

    @GET("filters")
    suspend fun getFiltros(): List<Filtro>

    @GET("users/{id}/friends")
    suspend fun getAmigosDoUtilizador(@Path("id") userId: Int): List<Utilizador>

    @POST("users/{id}/friends")
    suspend fun adicionarAmigo(@Path("id") userId: Int, @Body amigoRequest: Amigo): Response<Amigo>

    @DELETE("users/{id}/friends/{friendId}")
    suspend fun removerAmigo(@Path("id") userId: Int, @Path("friendId") amigoId: Int): Response<Unit>

    @GET("users/{id}/followers")
    suspend fun getSeguidores(@Path("id") userId: Int): List<Utilizador>

    @GET("users/{id}/following")
    suspend fun getAQuemSegue(@Path("id") userId: Int): List<Utilizador>

    @POST("users/{id}/follow")
    suspend fun seguirUtilizador(@Path("id") userIdToFollow: Int): Response<Seguidor>
    @DELETE("users/{id}/follow")
    suspend fun deixarDeSeguir(@Path("id") userIdToUnfollow: Int): Response<Unit>

    @GET("invitations")
    suspend fun getMeusConvites(): List<Convite>

    @GET("users/{id}/invitations")
    suspend fun getConvitesDoUtilizador(@Path("id") userId: Int): List<Convite>

    @POST("invitations")
    suspend fun enviarConvite(@Body novoConvite: Convite): Response<Convite>

    @PUT("invitations/{id}")
    suspend fun responderConvite(@Path("id") conviteId: Int, @Body resposta: Convite): Response<Convite>
}
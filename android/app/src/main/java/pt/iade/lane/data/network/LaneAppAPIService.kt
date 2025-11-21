package pt.iade.lane.data.network

import pt.iade.lane.data.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LaneAppAPIService {
    @GET("api/users/users")
    suspend fun getTodosUtilizadores(): List<Utilizador>

    @POST("api/users/users")
    suspend fun registarUtilizador(@Body request: RegisterRequestDTO): Response<Utilizador>

    @POST("api/users/login")
    suspend fun loginUtilizador(@Body request: LoginRequestDTO): Response<LoginResponseDTO>

    @DELETE("api/users/delete/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @GET("api/events/events")
    suspend fun getTodosEventos(): List<Evento>

    @POST("api/events/create/events")
    suspend fun criarEvento(@Body request: CreateEventDTO): Response<Evento>

    @DELETE("api/events/delete/{id}")
    suspend fun deleteEvent(@Path("id") id: Int): Response<Unit>

    @GET("api/filters/get/filters")
    suspend fun getFiltros(): Response<List<Filtro>>

    @POST("api/filters/create/filters")
    suspend fun createFilters(@Body filters: Filtro): Response<Filtro>

    @DELETE("api/filters/{id}")
    suspend fun deleteFilters(@Path("id") id: Int): Response<Unit>

    @GET("api/followers/all")
    suspend fun getAllFollowers(): List<Seguidor>

    @POST("api/followers/add")
    suspend fun addFollower(@Body follower: Seguidor): Response<String>

    @DELETE("api/followers/{id}")
    suspend fun deleteFollower(@Path("id") id: Int): Response<String>

    @GET("api/invitations/invitations")
    suspend fun getAllInvitations(): List<Convite>

    @POST("api/invitations/create/invitations")
    suspend fun createInvitation(@Body invitation: Convite): Response<Convite>

    @DELETE("api/invitations/{id}")
    suspend fun deleteInvitation(@Path("id") id: Int): Response<Unit>

    @POST("api/eparticipants/add")
    suspend fun addParticipant(@Body participants: EventParticipants): Response<String>

    @DELETE("api/eparticipants/{id}")
    suspend fun deleteParticipant(@Path("id") id: Int): Response<String>

    @GET("api/events/{id}/participants/count")
    suspend fun getParticipantsCount(
        @Path("id") eventId: Int
    ): Long

    @POST("api/events/{id}/participants/join")
    suspend fun joinEvent(
        @Path("id") eventId: Int,
        @Query("userId") userId: Int
    ): Response<Unit>
}
package pt.iade.lane.data.network

import pt.iade.lane.data.models.Convite
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.models.Seguidor
import pt.iade.lane.data.models.Utilizador
import pt.iade.lane.data.models.RegisterRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface LaneAppAPIService {

    @GET("api/users/users")
    suspend fun getTodosUtilizadores(): List<Utilizador>

    @POST("api/users/users")
    suspend fun registarUtilizador(@Body request: RegisterRequestDTO): Response<Utilizador>

    @GET("api/events/events")
    suspend fun getTodosEventos(): List<Evento>

    @POST("api/events/create/events")
    suspend fun criarEvento(@Body novoEvento: Evento): Response<Evento>

    @GET("api/filters/get/filters")
    suspend fun getFiltros(): List<Filtro>

    @GET("api/followers/all")
    suspend fun getTodosSeguidores(): List<Seguidor>

}
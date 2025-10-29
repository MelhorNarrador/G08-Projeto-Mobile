package pt.iade.lane.data.network

// Importa todos os 'Models' que criaste
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
 * Interface completa que define todos os endpoints da API Lane.
 * O Retrofit usa isto para gerar o código de networking.
 */
interface LaneAppAPIService {

    // --- Endpoints de Utilizadores (user_details) ---

    @GET("users")
    suspend fun getTodosUtilizadores(): List<Utilizador>

    @GET("users/{id}")
    suspend fun getUtilizadorPorId(@Path("id") userId: Int): Utilizador

    // Assumindo que a tua API tem um endpoint para registo
    // @POST("register")
    // suspend fun registarUtilizador(@Body novoUtilizador: RegisterRequest): Response<Utilizador>

    // Assumindo que a tua API tem um endpoint para login
    // @POST("login")
    // suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @PUT("users/{id}")
    suspend fun atualizarUtilizador(@Path("id") userId: Int, @Body utilizador: Utilizador): Response<Utilizador>


    // --- Endpoints de Eventos (events) ---

    @GET("events")
    suspend fun getTodosEventos(): List<Evento>

    // Exemplo de como usar um filtro (ex: /events?category=musica)
    @GET("events")
    suspend fun getEventosPorCategoria(@Query("category") categoriaId: Int): List<Evento>

    @GET("events/{id}")
    suspend fun getEventoPorId(@Path("id") eventId: Int): Evento

    @POST("events")
    suspend fun criarEvento(@Body novoEvento: Evento): Response<Evento> // A API devolve o evento criado

    @PUT("events/{id}")
    suspend fun atualizarEvento(@Path("id") eventId: Int, @Body evento: Evento): Response<Evento>

    @DELETE("events/{id}")
    suspend fun apagarEvento(@Path("id") eventId: Int): Response<Unit> // Response<Unit> para respostas vazias


    // --- Endpoints de Filtros (filters) ---

    @GET("filters")
    suspend fun getFiltros(): List<Filtro>


    // --- Endpoints de Amigos (friends) ---
    // (Normalmente, estes são "nested" dentro de um utilizador)

    @GET("users/{id}/friends")
    suspend fun getAmigosDoUtilizador(@Path("id") userId: Int): List<Utilizador> // Retorna a lista de *Utilizadores* amigos

    @POST("users/{id}/friends")
    suspend fun adicionarAmigo(@Path("id") userId: Int, @Body amigoRequest: Amigo): Response<Amigo> // Envia o pedido

    @DELETE("users/{id}/friends/{friendId}")
    suspend fun removerAmigo(@Path("id") userId: Int, @Path("friendId") amigoId: Int): Response<Unit>


    // --- Endpoints de Seguidores (followers) ---

    @GET("users/{id}/followers")
    suspend fun getSeguidores(@Path("id") userId: Int): List<Utilizador> // Lista de quem *segue* o 'userId'

    @GET("users/{id}/following")
    suspend fun getAQuemSegue(@Path("id") userId: Int): List<Utilizador> // Lista de quem o 'userId' *está a seguir*

    @POST("users/{id}/follow")
    suspend fun seguirUtilizador(@Path("id") userIdToFollow: Int): Response<Seguidor> // Seguir o 'userIdToFollow'

    @DELETE("users/{id}/follow")
    suspend fun deixarDeSeguir(@Path("id") userIdToUnfollow: Int): Response<Unit>


    // --- Endpoints de Convites (invitations) ---

    @GET("invitations") // A API deve devolver só os do utilizador logado
    suspend fun getMeusConvites(): List<Convite>

    // Ou, se a API for explícita:
    @GET("users/{id}/invitations")
    suspend fun getConvitesDoUtilizador(@Path("id") userId: Int): List<Convite>

    @POST("invitations")
    suspend fun enviarConvite(@Body novoConvite: Convite): Response<Convite>

    @PUT("invitations/{id}") // Para aceitar ou rejeitar
    suspend fun responderConvite(@Path("id") conviteId: Int, @Body resposta: Convite): Response<Convite>
}
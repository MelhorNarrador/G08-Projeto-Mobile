package pt.iade.lane.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa a *relação* de amizade entre dois utilizadores.
 * "Espelho" da tabela 'friends'.
 *
 * NOTA: Frequentemente, uma API REST para "amigos" (ex: GET /users/1/friends)
 * devolveria uma lista de 'Utilizador', e não esta classe.
 * Mas esta classe é útil se quiseres obter o registo da amizade em si.
 */
data class Amigo(
    @SerializedName("friends_id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int, // O utilizador que iniciou a ligação

    @SerializedName("friends_user_id")
    val amigoUserId: Int, // O utilizador que é o amigo

    @SerializedName("created_at")
    val criadoEm: Date
)
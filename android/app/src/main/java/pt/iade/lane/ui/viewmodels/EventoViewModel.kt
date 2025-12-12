package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.state.UserSessionUiState

open class EventoViewModel : ViewModel() {

    private val repository = EventoRepository()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    open val eventos: StateFlow<List<Evento>> = _eventos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _filtros = MutableStateFlow<List<Filtro>>(emptyList())
    val filtros: StateFlow<List<Filtro>> = _filtros
    private lateinit var sessionManager: SessionManager
    private val _session = MutableStateFlow(UserSessionUiState())
    val session = _session.asStateFlow()
    fun attachSessionManager(sm: SessionManager) {
        sessionManager = sm
        loadSession()
    }

    fun loadSession() {
        if (!::sessionManager.isInitialized) return

        val usernameRaw = sessionManager.fetchUserUsername().orEmpty()
        val userId = sessionManager.fetchUserId()

        _session.value = UserSessionUiState(
            userId = userId,
            name = sessionManager.fetchUserName().orEmpty(),
            username = if (usernameRaw.isNotEmpty()) "@$usernameRaw" else "",
            bio = sessionManager.fetchUserBio().orEmpty(),
            profileImageBase64 = sessionManager.fetchUserProfileImage(),
            joinedEventIds = sessionManager.fetchJoinedEvents().toSet(),
            isLoggedIn = userId != null
        )
    }

    fun markJoined(eventId: Int) {
        if (!::sessionManager.isInitialized) return
        sessionManager.addJoinedEvent(eventId)
        _session.update { it.copy(joinedEventIds = it.joinedEventIds + eventId) }
    }

    fun markLeft(eventId: Int) {
        if (!::sessionManager.isInitialized) return
        sessionManager.removeJoinedEvent(eventId)
        _session.update { it.copy(joinedEventIds = it.joinedEventIds - eventId) }
    }

    fun logout() {
        if (!::sessionManager.isInitialized) {
            _session.value = UserSessionUiState()
            return
        }
        sessionManager.clearAuth()
        _session.value = UserSessionUiState()
    }
    fun carregarFiltros() {
        viewModelScope.launch {
            try {
                val lista = repository.getFiltros()
                _filtros.value = lista
            } catch (e: Exception) {
                Log.e("EventoViewModel", "Erro ao carregar filtros: ${e.message}", e)
            }
        }
    }

    open fun carregarEventos() {
        Log.d("EventoViewModel", "A carregar eventos...")

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val lista = repository.getTodosEventos()
                _eventos.value = lista
                Log.d("EventoViewModel", "Eventos carregados: ${lista.size}")
            } catch (e: Exception) {
                val msg = "Erro ao carregar eventos: ${e.message}"
                Log.e("EventoViewModel", msg, e)
                _errorMessage.value = msg
            } finally {
                _isLoading.value = false
            }
        }
    }
    suspend fun getParticipantsCount(eventId: Int): Int {
        return repository.getParticipantsCount(eventId)
    }

    suspend fun joinEvent(eventId: Int, userId: Int): EventoRepository.JoinResult {
        return repository.joinEvent(eventId, userId)
    }

    suspend fun leaveEvent(eventId: Int, userId: Int): EventoRepository.JoinResult {
        return repository.leaveEvent(eventId, userId)
    }

    suspend fun deleteEvent(eventId: Int): Boolean {
        val ok = repository.deleteEvento(eventId)
        if (ok) {
            carregarEventos()
        }
        return ok
    }
}

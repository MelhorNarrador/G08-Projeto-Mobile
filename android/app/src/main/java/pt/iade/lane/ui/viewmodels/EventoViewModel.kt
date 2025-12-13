package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager

class EventoViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {
    private val repository = EventoRepository()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _filtros = MutableStateFlow<List<Filtro>>(emptyList())
    val filtros: StateFlow<List<Filtro>> = _filtros

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _refreshTrigger = MutableSharedFlow<Unit>()
    val refreshTrigger = _refreshTrigger.asSharedFlow()

    fun carregarFiltros() {
        viewModelScope.launch {
            try {
                _filtros.value = repository.getFiltros()
            } catch (e: Exception) {
                Log.e("EventoViewModel", "Erro ao carregar filtros: ${e.message}", e)
            }
        }
    }

    fun carregarEventos() {
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

    fun deleteEvent(eventoId: Int) {
        viewModelScope.launch {
            val success = repository.deleteEvento(eventoId)
            if (success) {
                carregarEventos()
                _toastMessage.emit("Evento apagado com sucesso.")
            } else {
                _toastMessage.emit("Erro ao apagar evento.")
            }
        }
    }

    fun leaveEvent(eventoId: Int) {
        val userId = sessionManager.fetchUserId()
        if (userId == null) {
            viewModelScope.launch { _toastMessage.emit("Utilizador não autenticado.") }
            return
        }

        viewModelScope.launch {
            when (val result = repository.leaveEvent(eventoId, userId)) {
                is EventoRepository.JoinResult.Success -> {
                    sessionManager.removeJoinedEvent(eventoId)
                    _refreshTrigger.emit(Unit)
                    carregarEventos()
                    _toastMessage.emit("Saíste do evento.")
                }
                is EventoRepository.JoinResult.Error -> {
                    _toastMessage.emit(result.message)
                }
                is EventoRepository.JoinResult.AlreadyJoined -> {
                    _toastMessage.emit("Estado inconsistente.")
                }
            }
        }
    }

    suspend fun getParticipantsCount(eventId: Int): Int {
        return repository.getParticipantsCount(eventId)
    }

    suspend fun joinEvent(eventId: Int, userId: Int): EventoRepository.JoinResult {
        return repository.joinEvent(eventId, userId)
    }
}
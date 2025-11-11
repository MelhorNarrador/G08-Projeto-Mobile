package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager
import java.lang.Exception

class CreateEventViewModel(
    private val repository: EventoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _filters = MutableStateFlow<List<Filtro>>(emptyList())
    val filters: StateFlow<List<Filtro>> = _filters
    private val _creationState = MutableStateFlow<EventCreationState>(EventCreationState.Idle)
    val creationState: StateFlow<EventCreationState> = _creationState
    fun loadFilters() {
        viewModelScope.launch {
            try {
                val filterList = repository.getFiltros()
                _filters.value = filterList
                Log.d("CreateEventViewModel", "Filtros carregados: ${filterList.size}")
            } catch (e: Exception) {
                Log.e("CreateEventViewModel", "Falha ao carregar filtros: ${e.message}")
            }
        }
    }
    fun createEvent(eventData: CreateEventDTO) {
        viewModelScope.launch {
            _creationState.value = EventCreationState.Loading
            try {
                // Pede ao repositório para criar o evento
                val eventoCriado = repository.criarEvento(eventData)

                if (eventoCriado != null) {
                    _creationState.value = EventCreationState.Success(eventoCriado)
                    Log.d("CreateEventViewModel", "Evento criado com sucesso: ${eventoCriado.titulo}")
                } else {
                    _creationState.value = EventCreationState.Error("Falha ao criar evento.")
                }
            } catch (e: Exception) {
                _creationState.value = EventCreationState.Error(e.message ?: "Erro de rede")
            }
        }
    }
    fun getCreatorId(): Int? {
        return sessionManager.fetchUserId()
    }
    class Factory(
        private val repository: EventoRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateEventViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CreateEventViewModel(repository, sessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
sealed class EventCreationState {
    object Idle : EventCreationState()
    object Loading : EventCreationState()
    data class Success(val event: Evento) : EventCreationState()
    data class Error(val message: String) : EventCreationState()
}
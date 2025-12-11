package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.CreateEventDTO
import pt.iade.lane.data.models.Filtro
import pt.iade.lane.data.repository.EventoRepository
import pt.iade.lane.data.utils.SessionManager

class CreateEventViewModel(
    private val repository: EventoRepository,
    private val sessionManager: SessionManager
    ) : ViewModel() {
    private val _filters = MutableStateFlow<List<Filtro>>(emptyList())
    val filters = _filters.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun loadFilters() {
        viewModelScope.launch {
            try {
                _filters.value = repository.getFiltros()
            } catch (e: Exception) {
                Log.e("CreateEventVM", "Erro filtros: ${e.message}")
            }
        }
    }

    fun createEvent(eventData: CreateEventDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isSuccess.value = false

            try {
                val eventoCriado = repository.criarEvento(eventData)

                if (eventoCriado != null) {
                    _isSuccess.value = true
                    Log.d("CreateEventVM", "Sucesso: ${eventoCriado.title}")
                } else {
                    _errorMessage.value = "Falha ao criar evento."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro de rede"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun clearError() {
        _errorMessage.value = null
    }

    fun getCreatorId(): Int? {
        return sessionManager.fetchUserId()
    }
    fun updateEvent(eventId: Int, dto: CreateEventDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            _isSuccess.value = false
            try {
                val ok = repository.updateEvento(eventId, dto)
                _isSuccess.value = ok
                if (!ok) {
                    _errorMessage.value = "Erro ao atualizar evento."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro desconhecido ao atualizar evento."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

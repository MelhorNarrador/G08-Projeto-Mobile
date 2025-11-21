package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.repository.EventoRepository

open class EventoViewModel : ViewModel(){
    private val repository = EventoRepository()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    open val eventos: StateFlow<List<Evento>> = _eventos
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
}
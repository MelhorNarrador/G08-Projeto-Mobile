package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.repository.EventoRepository
import java.lang.Exception

open class EventoViewModel(

    private val repository: EventoRepository
) : ViewModel() {


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

    class Factory(private val repository: EventoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventoViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
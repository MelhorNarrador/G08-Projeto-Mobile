package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.Evento
import pt.iade.lane.data.repository.EventoRepository

/**
 * ViewModel para gerir os dados relacionados com Eventos.
 * Ele sobrevive a mudanças de configuração (como rotação do ecrã).
 */
class EventoViewModel : ViewModel() {

    private val repository = EventoRepository()

    // 2. O 'LiveData' que vai guardar a nossa lista de eventos
    // É 'Mutable' (mutável) porque NÓS (o ViewModel) vamos alterá-lo.
    private val _eventos = MutableLiveData<List<Evento>>()

    val eventos: LiveData<List<Evento>> = _eventos


    fun carregarEventos() {
        Log.d("EventoViewModel", "A carregar eventos...")


        viewModelScope.launch {
            // 5. Chamar a função 'suspend' do repositório
            val lista = repository.getTodosEventos()


            _eventos.postValue(lista)

            Log.d("EventoViewModel", "Eventos carregados: ${lista.size}")
        }
    }
}
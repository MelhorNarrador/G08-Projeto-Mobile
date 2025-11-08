package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.RegisterRequestDTO
import pt.iade.lane.data.models.Utilizador
import pt.iade.lane.data.repository.UtilizadorRepository
import java.lang.Exception

class AuthViewModel(
    private val repository: UtilizadorRepository
) : ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registarUtilizador(request: RegisterRequestDTO) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading

            try {
                val utilizadorCriado = repository.registarUtilizador(request)

                if (utilizadorCriado != null) {
                    _registrationState.value = RegistrationState.Success(utilizadorCriado)
                    Log.d("AuthViewModel", "Registo com sucesso! Utilizador: ${utilizadorCriado.username}")
                } else {
                    _registrationState.value = RegistrationState.Error("Falha ao registar utilizador.")
                    Log.d("AuthViewModel", "Registo falhou (resposta não foi sucesso).")
                }
            } catch (e: Exception) {
                // Lida com erros de rede, etc.
                Log.e("AuthViewModel", "Exceção no registo: ${e.message}", e)
                _registrationState.value = RegistrationState.Error(e.message ?: "Erro de rede")
            }
        }
    }
    class Factory(private val repository: UtilizadorRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val user: Utilizador) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
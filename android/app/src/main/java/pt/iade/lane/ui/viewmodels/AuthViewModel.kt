package pt.iade.lane.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.LoginRequestDTO     // <-- Importa o DTO de Login
import pt.iade.lane.data.models.LoginResponseDTO  // <-- Importa a Resposta de Login
import pt.iade.lane.data.models.RegisterRequestDTO
import pt.iade.lane.data.models.Utilizador
import pt.iade.lane.data.repository.UtilizadorRepository
import pt.iade.lane.data.utils.SessionManager     // <-- Importa o SessionManager
import java.lang.Exception

class AuthViewModel(
    private val repository: UtilizadorRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
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
    fun loginUtilizador(request: LoginRequestDTO) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = repository.loginUtilizador(request)

                if (response != null) {
                    // SUCESSO! Guarda o token e o ID
                    sessionManager.saveAuth(response.token, response.userId)
                    _loginState.value = LoginState.Success
                    Log.d("AuthViewModel", "Login com sucesso! Token: ${response.token}")
                } else {
                    // Erro (Password errada, user não existe)
                    _loginState.value = LoginState.Error("Email ou password inválidos.")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exceção no login: ${e.message}", e)
                _loginState.value = LoginState.Error(e.message ?: "Erro de rede")
            }
        }
    }
    class Factory(
        private val repository: UtilizadorRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository, sessionManager) as T
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
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
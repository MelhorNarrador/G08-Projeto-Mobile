package pt.iade.lane.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.iade.lane.data.models.LoginRequestDTO
import pt.iade.lane.data.models.RegisterRequestDTO
import pt.iade.lane.data.models.Utilizador
import pt.iade.lane.data.repository.UtilizadorRepository
import pt.iade.lane.data.utils.SessionManager

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = UtilizadorRepository()
            private val sessionManager = SessionManager(application.applicationContext)

            private val _registrationState =
        MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

            private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun registarUtilizador(request: RegisterRequestDTO) {
        viewModelScope.launch { _registrationState.value = RegistrationState.Loading
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
                    sessionManager.saveAuth(response.token, response.userId)
                    pt.iade.lane.data.network.RetrofitClient.authToken = response.token
                    _loginState.value = LoginState.Success
                    Log.d("AuthViewModel", "Login OK! Token configurado: ${response.token}")
                    try {
                        val todosUtilizadores = repository.getTodosUtilizadores()
                        val currentUser: Utilizador? =
                            todosUtilizadores.firstOrNull { it.id == response.userId }

                        if (currentUser != null) {
                            sessionManager.saveUserProfile(
                                name = currentUser.nome,
                                username = currentUser.username,
                                email = currentUser.email
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "AuthViewModel",
                            "Erro ao obter dados do user depois do login: ${e.message}",
                            e
                        )
                    }
                } else {
                    _loginState.value = LoginState.Error("Email ou password inválidos.")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exceção no login: ${e.message}", e)
                _loginState.value = LoginState.Error(e.message ?: "Erro de rede")
            }
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
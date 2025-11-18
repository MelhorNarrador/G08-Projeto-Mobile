package pt.iade.lane.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.iade.lane.R
import pt.iade.lane.ui.theme.LaneBlue
import pt.iade.lane.ui.theme.LanePurple
import pt.iade.lane.ui.theme.LaneTheme
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import pt.iade.lane.data.models.LoginRequestDTO
import pt.iade.lane.data.utils.SessionManager
import pt.iade.lane.ui.viewmodels.LoginState
import pt.iade.lane.data.repository.UtilizadorRepository
import pt.iade.lane.ui.viewmodels.AuthViewModel
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel


class LoginActivity : ComponentActivity() {
    private val utilizadorRepository = UtilizadorRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(applicationContext)

        setContent {
            LaneTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModel.Factory(utilizadorRepository, sessionManager)
                )

                val loginState by authViewModel.loginState.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(loginState) {
                    when (val state = loginState) {
                        is LoginState.Success -> {
                            Toast.makeText(context, "Login com sucesso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        is LoginState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
                LoginScreen(
                    isLoading = loginState is LoginState.Loading,
                    onLoginClick = { email, password ->
                        val request = LoginRequestDTO(email, password)
                        authViewModel.loginUtilizador(request)
                    },
                    onRegisterClick = {
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}
@Composable
fun LoginScreen(
    isLoading: Boolean,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3A82F8),
            Color(0xFF9046FE)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo da Lane",
                modifier = Modifier.size(200.dp)
            )
            /* Comentado por agora, caso queira-mos manter no futuro
            Text(
                text = "A tua rota",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )*/

            Spacer(modifier = Modifier.height(48.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Nome de utilizador ou email") },
                colors = TextFieldDefaults.colors(
                ),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Palavra-passe") },
                colors = TextFieldDefaults.colors(
                ),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.3f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Iniciar Sessão", color = Color.White)
                }
            }

            TextButton(
                onClick = { onRegisterClick() },
                enabled = !isLoading
            ) {
                Text("Não tens uma conta? Regista-te", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LaneTheme {
        LoginScreen(
            isLoading = false,
            onLoginClick = { _, _ -> },
            onRegisterClick = {}
        )
    }
}
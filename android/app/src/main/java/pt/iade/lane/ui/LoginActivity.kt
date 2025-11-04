package pt.iade.lane.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Activity para o ecrã de Login.
 * Esta será a nossa nova "porta de entrada" da app.
 */
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen(
                    onLoginClick = { email, password ->
                        // --- O NOSSO "BYPASS" TEMPORÁRIO ---
                        // Como pediste, não validamos, apenas navegamos.

                        // No futuro, chamaríamos:
                        // authViewModel.login(email, password)

                        // 1. Criar a intenção de ir para a MainActivity
                        val intent = Intent(this, MainActivity::class.java)

                        // 2. Iniciar a MainActivity
                        startActivity(intent)

                        // 3. Fechar esta LoginActivity (para o utilizador não
                        //    poder "voltar atrás" para o login)
                        finish()
                    },
                    onRegisterClick = {
                        // No futuro, navegaria para a RegisterActivity
                        // val intent = Intent(this, RegisterActivity::class.java)
                        // startActivity(intent)
                    }
                )
            }
        }
    }
}

/**
 * O Composable que desenha o ecrã de Login
 */
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    // Estados para guardar o que o utilizador escreve
    // (Inspirado no RegisterActivity do SoundMarket)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("LANE", style = MaterialTheme.typography.headlineLarge) // Título

        Spacer(modifier = Modifier.height(48.dp))

        // Campo de Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Nome de utilizador ou email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Palavra-passe") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botão de Login
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sessão")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de Registo
        TextButton(onClick = { onRegisterClick() }) {
            Text("Não tens uma conta? Regista-te")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLoginClick = { _, _ -> }, onRegisterClick = {})
    }
}
package pt.iade.lane.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.iade.lane.data.models.RegisterRequestDTO
import pt.iade.lane.data.repository.UtilizadorRepository
import pt.iade.lane.ui.theme.LaneTheme
import pt.iade.lane.ui.viewmodels.AuthViewModel
import pt.iade.lane.ui.viewmodels.RegistrationState
import pt.iade.lane.data.utils.SessionManager
import java.util.Calendar
import androidx.compose.runtime.Composable

class RegisterActivity : ComponentActivity() {

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

                val registrationState by authViewModel.registrationState.collectAsState()
                val context = LocalContext.current
                LaunchedEffect(registrationState) {
                    when (val state = registrationState) {
                        is RegistrationState.Success -> {
                            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        is RegistrationState.Error -> {
                            Toast.makeText(context, "Erro: ${state.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }

                RegisterScreen(
                    isLoading = registrationState is RegistrationState.Loading,
                    onRegisterClick = { request ->
                        authViewModel.registarUtilizador(request)
                    },
                    onLoginClick = {
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    isLoading: Boolean,
    onRegisterClick: (RegisterRequestDTO) -> Unit,
    onLoginClick: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }

    var genderDropdownExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Masculino", "Feminino", "Outro", "Prefiro Nao Dizer")

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->

            val mesFormatado = (month + 1).toString().padStart(2, '0')
            val diaFormatado = day.toString().padStart(2, '0')

            dataNascimento = "$year-$mesFormatado-$diaFormatado"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A82F8), Color(0xFF9046FE))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Criar Conta", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = loginTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = username, onValueChange = { username = it }, label = { Text("Nome de utilizador") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = loginTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = loginTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Palavra-passe") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), visualTransformation = PasswordVisualTransformation(), colors = loginTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = dataNascimento,
                onValueChange = {},
                label = { Text("Data de Nascimento (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                readOnly = true,
                colors = loginTextFieldColors(),
                enabled = false
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = genderDropdownExpanded,
                onExpandedChange = { genderDropdownExpanded = !genderDropdownExpanded }
            ) {
                TextField(
                    value = genero,
                    onValueChange = {},
                    label = { Text("Género") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown") },
                    colors = loginTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = genderDropdownExpanded,
                    onDismissRequest = { genderDropdownExpanded = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                genero = option
                                genderDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val request = RegisterRequestDTO(
                        nome = nome,
                        username = username,
                        email = email,
                        passwordPura = password,
                        dataNascimento = dataNascimento,
                        genero = when (genero) {
                            "Prefiro Nao Dizer" -> "Prefiro_nao_dizer"
                            "Masculino" -> "Masculino"
                            "Feminino" -> "Feminino"
                            "Outro" -> "Outro"
                            else -> genero
                        }
                    )
                    onRegisterClick(request)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Criar Conta", color = Color.White)
                }
            }

            TextButton(onClick = { onLoginClick() }) {
                Text("Já tens uma conta? Iniciar Sessão", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.LightGray,
    disabledLabelColor = Color.LightGray,
    cursorColor = Color.White,
    focusedContainerColor = Color.White.copy(alpha = 0.1f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
    disabledContainerColor = Color.White.copy(alpha = 0.1f),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent
)

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    LaneTheme {
        RegisterScreen(isLoading = false, onRegisterClick = {}, onLoginClick = {})
    }
}
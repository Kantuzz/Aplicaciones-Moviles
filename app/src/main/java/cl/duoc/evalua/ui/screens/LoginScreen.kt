package cl.duoc.evalua.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.evalua.R
import cl.duoc.evalua.data.datastore.SessionStore
import cl.duoc.evalua.viewmodel.AuthUiState
import cl.duoc.evalua.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoggedIn: () -> Unit) {
    val app = LocalContext.current.applicationContext as Application

    // Factory simple para inyectar SessionStore sin DI
    val vm: AuthViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(SessionStore(app)) as T
        }
    })

    val ui: AuthUiState by vm.ui.collectAsState()

    Scaffold { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Logo arriba ---
                Image(
                    painter = painterResource(id = R.drawable.duoc_logo),
                    contentDescription = "Duoc UC",
                    modifier = Modifier.size(96.dp)
                )

                Text("Ingreso docente", style = MaterialTheme.typography.headlineSmall)

                // --- Tarjeta con el formulario ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = ui.email,
                            onValueChange = vm::onEmailChange,
                            label = { Text("Correo institucional") },
                            singleLine = true,
                            isError = ui.emailError != null,
                            supportingText = {
                                AnimatedVisibility(ui.emailError != null) {
                                    Text(
                                        ui.emailError ?: "",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = ui.password,
                            onValueChange = vm::onPasswordChange,
                            label = { Text("Contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = ui.passwordError != null,
                            supportingText = {
                                AnimatedVisibility(ui.passwordError != null) {
                                    Text(
                                        ui.passwordError ?: "",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { vm.login(onLoggedIn) },
                            enabled = ui.loginEnabled && !ui.loading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (ui.loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Ingresar")
                            }
                        }

                        Text(
                            text = "Solo se permiten correos con: @profesor.duoc.cl",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

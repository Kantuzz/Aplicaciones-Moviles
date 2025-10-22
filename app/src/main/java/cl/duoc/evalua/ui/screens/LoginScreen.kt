package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import cl.duoc.evalua.R

private const val ALLOWED_DOMAIN = "profesor.duoc.cl" // ← cambia aquí si lo necesitas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.duoc_logo),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Evaluación Gastronomía")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LoginContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            onLoggedIn = onLoggedIn
        )
    }
}

@Composable
private fun LoginContent(
    modifier: Modifier = Modifier,
    onLoggedIn: () -> Unit
) {
    val focus = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val emailError = remember(email) { email.isNotBlank() && !isValidEmailWithDomain(email, ALLOWED_DOMAIN) }
    val passError = remember(pass) { pass.isNotBlank() && !isValidPassword(pass) }

    val isValid = isValidEmailWithDomain(email, ALLOWED_DOMAIN) && isValidPassword(pass)

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.duoc_logo),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(96.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Usuario") },
            placeholder = { Text("nombre@$ALLOWED_DOMAIN") },
            singleLine = true,
            isError = emailError,
            supportingText = {
                if (emailError) Text("Debe ser un correo válido del dominio @$ALLOWED_DOMAIN")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            singleLine = true,
            isError = passError,
            supportingText = {
                if (passError) Text("Mínimo 8 caracteres, 1 mayúscula, 1 minúscula y 1 dígito")
            },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPass) "Ocultar" else "Mostrar"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                if (isValid) {
                    focus.clearFocus()
                    onLoggedIn()
                }
            })
        )

        Button(
            enabled = isValid,
            onClick = {
                focus.clearFocus()
                onLoggedIn()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Ingresar")
        }
    }
}

/* ------------------ Validaciones ------------------ */

private fun isValidEmailWithDomain(email: String, domain: String): Boolean {
    val idx = email.indexOf('@')
    if (idx <= 0 || idx >= email.length - 1) return false
    val local = email.substring(0, idx)
    val host = email.substring(idx + 1)
    // local (antes de @) mínimo 1 char, host EXACTO al dominio permitido
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+$")
    return local.matches(emailRegex) && host.equals(domain, ignoreCase = true)
}

private fun isValidPassword(pass: String): Boolean {
    if (pass.length < 8) return false
    val hasUpper = pass.any { it.isUpperCase() }
    val hasLower = pass.any { it.isLowerCase() }
    val hasDigit = pass.any { it.isDigit() }
    return hasUpper && hasLower && hasDigit
}

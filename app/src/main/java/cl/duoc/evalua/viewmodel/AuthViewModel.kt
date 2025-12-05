package cl.duoc.evalua.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.evalua.data.datastore.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val loading: Boolean = false,
    val loginEnabled: Boolean = false
)

class AuthViewModel(private val session: SessionStore) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui = _ui.asStateFlow()

    fun onEmailChange(value: String) {
        val err = validarEmailDocente(value)
        _ui.value = _ui.value.copy(email = value, emailError = err)
        recomputeEnabled()
    }

    fun onPasswordChange(value: String) {
        val err = validarPassword(value)
        _ui.value = _ui.value.copy(password = value, passwordError = err)
        recomputeEnabled()
    }

    private fun recomputeEnabled() {
        val s = _ui.value
        _ui.value = s.copy(
            loginEnabled = s.emailError == null &&
                    s.passwordError == null &&
                    s.email.isNotBlank() &&
                    s.password.isNotBlank()
        )
    }

    fun login(onSuccess: () -> Unit) {
        val s = _ui.value
        if (!s.loginEnabled) return
        viewModelScope.launch {
            _ui.value = s.copy(loading = true)

            // Para Entrega 1: autenticación "mock".
            // Si el email es de dominio correcto y el pass cumple reglas -> éxito.
            session.saveLogin(email = s.email.trim())

            _ui.value = _ui.value.copy(loading = false)
            onSuccess()
        }
    }
}

// =========================
// Helpers reutilizables
// =========================

private val DOCENTE_REGEX =
    Regex("""^[A-Za-z0-9._%+-]+@profesor\.duoc\.cl$""")

/**
 * Valida un correo de docente:
 * - No vacío
 * - Dominio @profesor.duoc.cl
 * Devuelve mensaje de error o null si es válido.
 */
fun validarEmailDocente(email: String): String? {
    val v = email.trim()
    return when {
        v.isBlank() -> "Ingresa tu correo institucional"
        !DOCENTE_REGEX.matches(v) -> "Debe ser @profesor.duoc.cl"
        else -> null
    }
}

/**
 * Valida contraseña:
 * - No vacía
 * - Mínimo 6 caracteres
 * Devuelve mensaje de error o null si es válida.
 */
fun validarPassword(password: String): String? {
    return when {
        password.isBlank() -> "Ingresa tu contraseña"
        password.length < 6 -> "Mínimo 6 caracteres"
        else -> null
    }
}

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

    // Solo correos institucionales de docentes:
    private val domainRegex = Regex("""^[A-Za-z0-9._%+-]+@profesor\.duoc\.cl$""")

    fun onEmailChange(value: String) {
        val v = value.trim()
        val err = when {
            v.isBlank() -> "Ingresa tu correo institucional"
            !domainRegex.matches(v) -> "Debe ser @profesor.duoc.cl"
            else -> null
        }
        _ui.value = _ui.value.copy(email = value, emailError = err)
        recomputeEnabled()
    }

    fun onPasswordChange(value: String) {
        val err = when {
            value.isBlank() -> "Ingresa tu contraseña"
            value.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
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
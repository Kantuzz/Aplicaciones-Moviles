package cl.duoc.evalua.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.evalua.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DolarState(
    val isLoading: Boolean = false,
    val valor: Double? = null,
    val error: String? = null
)

class DolarViewModel : ViewModel() {

    private val _state = MutableStateFlow(DolarState(isLoading = true))
    val state: StateFlow<DolarState> = _state

    init {
        cargarDolar()
    }

    fun cargarDolar() {
        viewModelScope.launch {
            _state.value = DolarState(isLoading = true)
            try {
                val response = RetrofitClient.api.getDolar()
                val valor = response.serie.firstOrNull()?.valor
                _state.value = DolarState(
                    isLoading = false,
                    valor = valor,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = DolarState(
                    isLoading = false,
                    valor = null,
                    error = "Error al obtener valor del dólar"
                )
            }
        }
    }
}

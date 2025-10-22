package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.evalua.data.repo.CriteriosRepository
import cl.duoc.evalua.data.repo.EvaluacionesRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionAnonimaScreen(mesaId: String) {
    val criteriosRepo = remember { CriteriosRepository() }
    val evalRepo = remember { EvaluacionesRepository() }
    val criterios by criteriosRepo.observeActivos().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Mapa de valores 1..5 por criterioNombre
    var valores by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var comentario by remember { mutableStateOf("") }
    val puedeEnviar = criterios.isNotEmpty() && criterios.all { (valores[it.nombre] ?: 0) in 1..5 }

    Scaffold(topBar = { TopAppBar(title = { Text("Evaluación anónima") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text("Mesa: $mesaId", style = MaterialTheme.typography.titleMedium)

            criterios.forEach { c ->
                val actual = valores[c.nombre] ?: 3
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${c.orden}. ${c.nombre}", style = MaterialTheme.typography.titleMedium)
                    Slider(
                        value = actual.toFloat(),
                        onValueChange = { v -> valores = valores.toMutableMap().apply { put(c.nombre, v.toInt()) } },
                        valueRange = 1f..5f,
                        steps = 3 // 2..4..(1..5)
                    )
                    Text("Puntaje: $actual / 5", style = MaterialTheme.typography.bodySmall)
                }
                Divider()
            }

            OutlinedTextField(
                value = comentario,
                onValueChange = { if (it.length <= 280) comentario = it },
                label = { Text("Comentario (opcional, máx. 280)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                enabled = puedeEnviar,
                onClick = {
                    scope.launch {
                        val snapshot = criterios.map { it.nombre to it.peso } // (nombre, pesoSnapshot)
                        val uuidMesa = runCatching { UUID.fromString(mesaId) }.getOrNull()
                        if (uuidMesa != null) {
                            evalRepo.guardar(
                                mesaId = uuidMesa,
                                jornadaId = null, // si luego agregas Jornada, pásala aquí
                                comentario = comentario.ifBlank { null },
                                snapshot = snapshot,
                                valores = valores
                            )
                        }
                        valores = emptyMap(); comentario = ""
                    }
                }
            ) { Text("Enviar evaluación") }
        }
    }
}

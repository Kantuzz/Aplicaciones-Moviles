package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.evalua.data.local.CriterioEntity
import cl.duoc.evalua.data.repo.CriteriosRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriteriosScreen() {
    val repo = remember { CriteriosRepository() }
    val lista by repo.observeTodos().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var orden by remember { mutableStateOf("0") }
    var peso by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Criterios De Evaluacion") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Del aspecto a Evaluar") }, singleLine = true)
            OutlinedTextField(value = orden, onValueChange = { orden = it.filter { ch -> ch.isDigit() } }, label = { Text("Orden") }, singleLine = true)
            OutlinedTextField(value = peso, onValueChange = { peso = it.filter { ch -> ch.isDigit() } }, label = { Text("Puntaje") }, singleLine = true)

            Button(
                enabled = nombre.trim().isNotEmpty(),
                onClick = {
                    scope.launch {
                        repo.upsert(
                            CriterioEntity(
                                id = UUID.randomUUID(),
                                nombre = nombre.trim(),
                                orden = orden.toIntOrNull() ?: 0,
                                peso = peso.toIntOrNull(),
                                activo = true
                            )
                        )
                        nombre = ""; orden = "0"; peso = ""
                    }
                }
            ) { Text("Agregar") }

            Divider()

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lista, key = { it.id }) { c ->
                    ElevatedCard {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("${c.orden}. ${c.nombre}", style = MaterialTheme.typography.titleMedium)
                                val pesoTxt = c.peso?.let { " • Puntaje: $it" } ?: ""
                                Text("Estado: ${if (c.activo) "Activo" else "Inactivo"}$pesoTxt", style = MaterialTheme.typography.bodySmall)
                            }
                            AssistChip(
                                onClick = {
                                    scope.launch {
                                        repo.upsert(c.copy(activo = !c.activo))
                                    }
                                },
                                label = { Text(if (c.activo) "Desactivar" else "Activar") }
                            )
                        }
                    }
                }
            }
        }
    }
}

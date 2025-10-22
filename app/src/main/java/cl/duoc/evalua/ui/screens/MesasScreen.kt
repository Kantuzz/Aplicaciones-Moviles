package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.evalua.data.local.MesaEntity
import cl.duoc.evalua.data.repo.MesasRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesasScreen() {
    val repo = remember { MesasRepository() }
    val scope = rememberCoroutineScope()
    val mesas by repo.observe().collectAsState(initial = emptyList())
    var nombre by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Mesas") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de mesa") },
                singleLine = true,
                isError = nombre.isNotBlank() && nombre.trim().length < 2
            )
            Button(
                enabled = nombre.trim().length >= 2,
                onClick = {
                    scope.launch { repo.upsert(nombre.trim()) ; nombre = "" }
                }
            ) { Text("Crear mesa") }

            Divider()

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(mesas, key = { it.id }) { m: MesaEntity ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(m.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("ID: ${m.id}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(
                                    onClick = {
                                        scope.launch { repo.upsertEntity(m.copy(activa = !m.activa)) }
                                    },
                                    label = { Text(if (m.activa) "Desactivar" else "Activar") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

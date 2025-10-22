package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cl.duoc.evalua.data.local.MesaEntity
import cl.duoc.evalua.data.repo.MesasRepository
import cl.duoc.evalua.ui.navigation.Route
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesasScreen(nav: NavHostController) {
    val repo = remember { MesasRepository() }
    val scope = rememberCoroutineScope()
    val mesas by repo.observe().collectAsState(initial = emptyList())

    var nombre by remember { mutableStateOf("") }
    val puedeCrear = nombre.trim().length >= 2

    var renombrando: MesaEntity? by remember { mutableStateOf(null) }
    var nuevoNombre by remember { mutableStateOf("") }

    var aEliminar: MesaEntity? by remember { mutableStateOf(null) }

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
                enabled = puedeCrear,
                onClick = {
                    scope.launch { repo.upsert(nombre.trim()); nombre = "" }
                }
            ) { Text("Crear mesa") }

            Divider()

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(mesas, key = { it.id }) { m: MesaEntity ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Box {
                            // ❌ Botón de eliminar en esquina superior derecha
                            IconButton(
                                onClick = { aEliminar = m },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar mesa",
                                    tint = Color(0xFFFFC107) // Amarillo igual al botón "Ver evaluación"
                                )
                            }

                            Column(
                                Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(m.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("ID: ${m.id}", style = MaterialTheme.typography.bodySmall)

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(onClick = {
                                        nav.navigate(Route.Evaluar.pathFor(m.id.toString()))
                                    }) { Text("Ver evaluación") }

                                    AssistChip(
                                        onClick = { scope.launch { repo.upsertEntity(m.copy(activa = !m.activa)) } },
                                        label = { Text(if (m.activa) "Desactivar" else "Activar") }
                                    )

                                    AssistChip(
                                        onClick = {
                                            renombrando = m
                                            nuevoNombre = m.nombre
                                        },
                                        label = { Text("Renombrar") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo renombrar
    if (renombrando != null) {
        AlertDialog(
            onDismissRequest = { renombrando = null },
            title = { Text("Renombrar mesa") },
            text = {
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = { nuevoNombre = it },
                    label = { Text("Nuevo nombre") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = nuevoNombre.trim().isNotEmpty(),
                    onClick = {
                        val id = renombrando!!.id
                        val nn = nuevoNombre.trim()
                        renombrando = null
                        scope.launch { repo.rename(id, nn) }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { renombrando = null }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo eliminar
    if (aEliminar != null) {
        val mesa = aEliminar!!
        AlertDialog(
            onDismissRequest = { aEliminar = null },
            title = { Text("Eliminar mesa") },
            text = { Text("¿Seguro que deseas eliminar «${mesa.nombre}»? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    aEliminar = null
                    scope.launch { repo.delete(mesa) }
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { aEliminar = null }) { Text("Cancelar") } }
        )
    }
}

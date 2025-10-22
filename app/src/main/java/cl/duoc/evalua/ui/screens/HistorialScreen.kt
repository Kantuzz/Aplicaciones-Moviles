package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.evalua.data.local.MesaEntity
import cl.duoc.evalua.data.local.EvaluacionDetalleEntity
import cl.duoc.evalua.data.repo.EvaluacionesRepository
import cl.duoc.evalua.data.repo.MesasRepository
import kotlinx.coroutines.flow.emptyFlow
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen() {
    val mesasRepo = remember { MesasRepository() }
    val evalRepo = remember { EvaluacionesRepository() }

    val mesas by mesasRepo.observe().collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    var seleccion by remember { mutableStateOf<MesaEntity?>(null) }

    var rangoDias by remember { mutableStateOf(7) }
    val ahora = Instant.now()
    val desde = remember(rangoDias) { ahora.minus(rangoDias.toLong(), ChronoUnit.DAYS) }

    val flow = remember(seleccion, desde, ahora) {
        seleccion?.let { evalRepo.observePorMesaYRango(it.id, desde, ahora) } ?: emptyFlow()
    }
    val lista by flow.collectAsState(initial = emptyList())

    val promedios: Map<String, Double> = remember(lista) {
        val mapa = linkedMapOf<String, MutableList<Int>>()
        lista.forEach { ev ->
            ev.detalles.forEach { d -> mapa.getOrPut(d.criterioNombre) { mutableListOf() }.add(d.valor) }
        }
        mapa.mapValues { (_, values) -> if (values.isNotEmpty()) values.sum().toDouble() / values.size else 0.0 }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Historial") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Selector mesa
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = seleccion?.nombre ?: "Selecciona mesa",
                    onValueChange = {},
                    label = { Text("Mesa") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    mesas.forEach { m ->
                        DropdownMenuItem(text = { Text(m.nombre) }, onClick = { seleccion = m; expanded = false })
                    }
                }
            }

            // Rango rápido
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = { rangoDias = 1 }, label = { Text("1 día") })
                AssistChip(onClick = { rangoDias = 7 }, label = { Text("7 días") })
                AssistChip(onClick = { rangoDias = 30 }, label = { Text("30 días") })
            }

            // KPIs
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Evaluaciones: ${lista.size}")
                    promedios.forEach { (crit, prom) ->
                        Text("$crit: ${"%.2f".format(prom)} / 5")
                    }
                }
            }

            // Lista cruda
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(lista) { pack ->
                    ElevatedCard {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Fecha: ${pack.eva.timestamp}")
                            DetallesFila(pack.detalles)
                            pack.eva.comentario?.let { Text("Comentario: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetallesFila(detalles: List<EvaluacionDetalleEntity>) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        detalles.forEach { d ->
            Text("${d.criterioNombre}: ${d.valor}")
        }
    }
}

package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.evalua.data.repo.CriteriosRepository
import cl.duoc.evalua.data.repo.EvaluacionesRepository
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.round
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionAnonimaScreen(mesaId: String) {
    val criteriosRepo = remember { CriteriosRepository() }
    val evalRepo = remember { EvaluacionesRepository() }
    val criterios by criteriosRepo.observeActivos().collectAsState(initial = emptyList())

    // Estado de la UI
    var valores by remember { mutableStateOf<Map<String, Int>>(emptyMap()) } // criterio -> 1..5
    var comentario by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var cooldown by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showThanks by remember { mutableStateOf(false) }

    // Validaciones
    val mesaUuid = remember(mesaId) { runCatching { UUID.fromString(mesaId) }.getOrNull() }
    val criteriosVacios = criterios.isEmpty()
    val todosPunteados = criterios.isNotEmpty() && criterios.all { (valores[it.nombre] ?: 0) in 1..5 }
    val puedeEnviar = mesaUuid != null && !criteriosVacios && todosPunteados && !sending && !cooldown

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evaluación anónima", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },

        // Botón SIEMPRE visible
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            sending = true
                            scope.launch {
                                try {
                                    val snapshot = criterios.map { it.nombre to it.peso }
                                    evalRepo.guardar(
                                        mesaId = mesaUuid!!,
                                        jornadaId = null,
                                        comentario = comentario.ifBlank { null },
                                        snapshot = snapshot,
                                        valores = valores
                                    )
                                    // Reset UI
                                    valores = emptyMap()
                                    comentario = ""

                                    // Feedback
                                    showThanks = true
                                    snackbar.showSnackbar("¡Gracias! Tu evaluación fue enviada.")

                                    // Cooldown anti doble-click (2s)
                                    cooldown = true
                                    sending = false

                                    // Ocultar banner luego de 1.8s y levantar cooldown
                                    scope.launch {
                                        kotlinx.coroutines.delay(1800)
                                        showThanks = false
                                    }
                                    scope.launch {
                                        kotlinx.coroutines.delay(2000)
                                        cooldown = false
                                    }
                                } catch (_: Exception) {
                                    sending = false
                                    snackbar.showSnackbar("No se pudo guardar. Intenta de nuevo.")
                                }
                            }
                        },
                        enabled = puedeEnviar,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(60.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        if (sending) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text("ENVIAR EVALUACIÓN", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    ) { pad ->
        // Contenido scrollable
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Banner “Gracias” animado (aparece tras enviar)
                AnimatedVisibility(
                    visible = showThanks,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            "¡Gracias! Tu evaluación fue enviada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Text("Mesa: $mesaId", style = MaterialTheme.typography.titleMedium)
                if (mesaUuid == null) {
                    AssistChip(onClick = {}, enabled = false, label = { Text("ID de mesa inválido") })
                }
            }

            if (criteriosVacios) {
                item {
                    ElevatedCard {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("No hay criterios activos", style = MaterialTheme.typography.titleMedium)
                            Text("El docente debe definirlos en el menú Criterios antes de recibir evaluaciones.")
                        }
                    }
                }
            }

            items(criterios, key = { it.id }) { c ->
                val actual = valores[c.nombre] ?: 3
                ElevatedCard {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("${c.orden}. ${c.nombre}", style = MaterialTheme.typography.titleMedium)
                        DiscreteSlider1to5(
                            value = actual,
                            onChange = { nuevo ->
                                valores = valores.toMutableMap().apply { put(c.nombre, nuevo) }
                            }
                        )
                        Text("Puntaje: $actual / 5", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { if (it.length <= 280) comentario = it },
                    label = { Text("Comentario (opcional)") },
                    supportingText = { Text("${comentarioLen(comentario)}/280") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                // Espacio para que el último campo no quede tapado por la bottomBar
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

// contador seguro para evitar recomposiciones raras
private fun comentarioLen(text: String) = text.length

/**
 * Slider discreto 1..5 (siempre retorna enteros)
 */
@Composable
private fun DiscreteSlider1to5(
    value: Int,
    onChange: (Int) -> Unit
) {
    var internal by remember(value) { mutableStateOf(value.toFloat()) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Slider(
            value = internal,
            onValueChange = { internal = it },
            valueRange = 1f..5f,
            steps = 3, // marca 2,3,4 entre 1 y 5
            onValueChangeFinished = {
                val snapped = round(internal).toInt().coerceIn(1, 5)
                internal = snapped.toFloat()
                onChange(snapped)
            }
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            (1..5).forEach { n -> Text("$n", style = MaterialTheme.typography.labelSmall) }
        }
    }
}

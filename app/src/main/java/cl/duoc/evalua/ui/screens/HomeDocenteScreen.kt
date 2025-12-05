package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cl.duoc.evalua.ui.navigation.Route
import cl.duoc.evalua.viewmodel.DolarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDocenteScreen(nav: NavHostController) {

    val dolarViewModel: DolarViewModel = viewModel()
    val dolarState by dolarViewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // fondo crema
        topBar = {
            TopAppBar(
                title = { Text("Evaluación Gastronomía") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,   // amarillo
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Menú docente", style = MaterialTheme.typography.titleLarge)

            // --- Bloque de API Dólar ---
            val errorMessage = dolarState.error
            when {
                dolarState.isLoading -> {
                    Text(text = "Cargando valor del dólar...")
                }
                errorMessage != null -> {
                    Text(text = errorMessage, color = Color.Red)
                }
                dolarState.valor != null -> {
                    Text(text = "Valor dólar CLP: ${dolarState.valor}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila 1
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    icon = Icons.Default.RestaurantMenu,
                    label = "Mesas",
                    modifier = Modifier.weight(1f)
                ) { nav.navigate(Route.Mesas.path) }

                MenuCard(
                    icon = Icons.Default.QrCode,
                    label = "Generar QR",
                    modifier = Modifier.weight(1f)
                ) { nav.navigate(Route.GenerarQR.path) }
            }

            // Fila 2
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    icon = Icons.Default.AccessTime,
                    label = "Historial",
                    modifier = Modifier.weight(1f)
                ) { nav.navigate(Route.Historial.path) }

                MenuCard(
                    icon = Icons.Default.Create,
                    label = "Criterios",
                    modifier = Modifier.weight(1f)
                ) { nav.navigate(Route.Criterios.path) }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = MaterialTheme.shapes.extraLarge, // esquinas redondeadas
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface // tarjeta clara
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

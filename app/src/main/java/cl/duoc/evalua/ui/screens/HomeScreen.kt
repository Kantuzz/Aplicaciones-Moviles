package cl.duoc.evalua.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.evalua.R
import cl.duoc.evalua.data.datastore.SessionStore
import cl.duoc.evalua.ui.theme.DuocBlack
import cl.duoc.evalua.ui.theme.DuocYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMesas: () -> Unit,
    onQR: () -> Unit,
    onHistorial: () -> Unit,
    onCriterios: () -> Unit,
    onLogout: () -> Unit
) {
    val app = LocalContext.current.applicationContext as Application
    val session = SessionStore(app)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.duoc_logo), // cambia si tu recurso se llama distinto
                            contentDescription = null,
                            tint = DuocBlack,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Evaluación Gastronomía")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                session.logout()
                                onLogout()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DuocYellow,
                    titleContentColor = DuocBlack,
                    actionIconContentColor = DuocBlack
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Menú docente", style = MaterialTheme.typography.titleLarge)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MenuCard(
                    title = "Mesas",
                    icon = { Icon(Icons.Filled.Restaurant, contentDescription = null) },
                    onClick = onMesas,
                    modifier = Modifier.weight(1f)
                )
                MenuCard(
                    title = "Generar QR",
                    icon = { Icon(Icons.Filled.QrCode, contentDescription = null) },
                    onClick = onQR,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MenuCard(
                    title = "Historial",
                    icon = { Icon(Icons.Filled.History, contentDescription = null) },
                    onClick = onHistorial,
                    modifier = Modifier.weight(1f)
                )
                MenuCard(
                    title = "Criterios",
                    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    onClick = onCriterios,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MenuCard(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

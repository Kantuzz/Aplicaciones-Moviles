package cl.duoc.evalua.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MesasScreen() {
    Scaffold { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Text("Mesas (lista y administración)", modifier = Modifier.align(Alignment.Center))
        }
    }
}

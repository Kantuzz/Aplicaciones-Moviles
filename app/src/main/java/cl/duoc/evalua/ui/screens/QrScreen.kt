package cl.duoc.evalua.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.evalua.data.local.MesaEntity
import cl.duoc.evalua.data.repo.MesasRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScreen() {
    val repo = remember { MesasRepository() }
    val mesas by repo.observe().collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    var seleccion by remember { mutableStateOf<MesaEntity?>(null) }
    val ctx = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Generar / Ver QR") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (seleccion == null && mesas.isNotEmpty()) seleccion = mesas.first()

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = seleccion?.nombre ?: "Selecciona mesa",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mesa") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    mesas.forEach { m ->
                        DropdownMenuItem(text = { Text(m.nombre) }, onClick = { seleccion = m; expanded = false })
                    }
                }
            }

            val deep = seleccion?.let { "app://eval?mesa=${it.id}" }
            val bmp = remember(deep) { deep?.let { makeQr(it, 720) } }

            if (bmp != null) {
                Image(bmp.asImageBitmap(), contentDescription = "QR", modifier = Modifier.size(300.dp))
                Text(deep ?: "", style = MaterialTheme.typography.bodySmall)
                Button(onClick = {
                    // Compartir el link del QR (simple y efectivo)
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, deep)
                    }
                    ctx.startActivity(Intent.createChooser(sendIntent, "Compartir QR"))
                }) { Text("Compartir link") }
            } else {
                Text("Crea y selecciona una mesa para ver su QR.")
            }
        }
    }
}

private fun makeQr(text: String, size: Int): Bitmap? = try {
    val bits = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) for (y in 0 until size) {
        bmp.setPixel(x, y, if (bits[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
    }
    bmp
} catch (_: Exception) { null }

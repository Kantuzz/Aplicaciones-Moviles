package cl.duoc.evalua.ui.screens

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.print.PrintHelper
import cl.duoc.evalua.data.local.MesaEntity
import cl.duoc.evalua.data.repo.MesasRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScreen() {
    val repo = remember { MesasRepository() }
    val mesas by repo.observe().collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }
    var seleccion by remember { mutableStateOf<MesaEntity?>(null) }
    val ctx = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Generar / Exportar QR") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (seleccion == null && mesas.isNotEmpty()) seleccion = mesas.first()

            // Selector de mesa
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

            // Generar QR a partir del deeplink
            val deeplink = seleccion?.let { "app://eval?mesa=${it.id}" }
            val bmp = remember(deeplink) { deeplink?.let { makeQr(it, 900) } }

            if (bmp != null && seleccion != null) {
                val nombreArchivo = "QR_${seleccion!!.nombre}_${seleccion!!.id}.png"

                Image(bmp.asImageBitmap(), contentDescription = "QR", modifier = Modifier.size(300.dp))
                Text(deeplink ?: "", style = MaterialTheme.typography.bodySmall)

                // Acciones
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        // Compartir el link simple (para WhatsApp/Correo)
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, deeplink)
                        }
                        ctx.startActivity(Intent.createChooser(sendIntent, "Compartir link"))
                    }) { Text("Compartir link") }

                    Button(onClick = {
                        // Guardar PNG en la galería (Pictures/EvaluacionQR)
                        val uri = saveBitmapToPictures(ctx, bmp, nombreArchivo)
                        if (uri != null) {
                            Toast.makeText(ctx, "QR guardado en Imágenes", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ctx, "No se pudo guardar el QR", Toast.LENGTH_SHORT).show()
                        }
                    }) { Text("Guardar PNG") }
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        // Compartir la imagen PNG (crea/usa el archivo guardado)
                        val uri = saveBitmapToPictures(ctx, bmp, nombreArchivo)
                        if (uri != null) {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            ctx.startActivity(Intent.createChooser(share, "Compartir imagen QR"))
                        } else {
                            Toast.makeText(ctx, "No se pudo compartir el QR", Toast.LENGTH_SHORT).show()
                        }
                    }) { Text("Compartir imagen") }

                    Button(onClick = {
                        // Imprimir con PrintHelper (usa servicios de impresión del sistema)
                        val ph = PrintHelper(ctx).apply { scaleMode = PrintHelper.SCALE_MODE_FIT }
                        ph.printBitmap("QR ${seleccion!!.nombre}", bmp)
                    }) { Text("Imprimir") }
                }
            } else {
                Text("Crea y selecciona una mesa para ver su QR.")
            }
        }
    }
}

/* ----------------- Helpers ----------------- */

private fun makeQr(text: String, size: Int): Bitmap? = try {
    val bits = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) for (y in 0 until size) {
        bmp.setPixel(x, y, if (bits[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
    }
    bmp
} catch (_: Exception) { null }

private fun saveBitmapToPictures(
    context: android.content.Context,
    bitmap: Bitmap,
    fileName: String
): Uri? {
    return try {
        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EvaluacionQR")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(collection, values) ?: return null

        // OutputStream puede ser null → chequeamos antes de usarlo
        resolver.openOutputStream(uri)?.use { out ->
            // No usar named params; firma = (format, quality, stream)
            val ok = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            if (!ok) return null
        } ?: return null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val done = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(uri, done, null, null)
        }

        uri
    } catch (_: Exception) {
        null
    }
}



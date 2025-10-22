package cl.duoc.evalua.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = DuocYellow,
    onPrimary = DuocBlack,

    secondary = DuocBlack,
    onSecondary = White,

    tertiary = DuocGray,

    background = BgCream,
    onBackground = DuocBlack,

    surface = SurfaceLight,
    onSurface = DuocBlack,

    error = Color(0xFFDA0000)
)

@Composable
fun EvaluacionGastroTheme(
    darkTheme: Boolean = false, // fuerza claro para mantener la marca
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography, // deja tu Type.kt como está
        content = content
    )
}

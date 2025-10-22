package cl.duoc.evalua.ui.navigation

sealed class Route(val path: String) {
    data object Login : Route("login")             // pantalla de login (tu diseño)
    data object Home : Route("home_docente")       // menú docente
    data object Mesas : Route("mesas")
    data object GenerarQR : Route("generar_qr")
    data object Historial : Route("historial")
    data object Criterios : Route("criterios")

    // pública: evaluación anónima abierta por QR
    data object Evaluar : Route("evaluar/{mesaId}") {
        fun pathFor(id: String) = "evaluar/$id"
        const val DeepLinkPattern = "app://eval?mesa={mesaId}"
    }
}

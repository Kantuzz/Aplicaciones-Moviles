package cl.duoc.evalua.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.NavHostController
import cl.duoc.evalua.ui.screens.*

@Composable
fun AppNavHost(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Route.Login.path) {

        // LOGIN (usa tu pantalla actual con callback onLoggedIn)
        composable(Route.Login.path) {
            LoginScreen(
                onLoggedIn = {
                    nav.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // HOME DOCENTE
        composable(Route.Home.path) { HomeDocenteScreen(nav) }
        composable(Route.Mesas.path) { MesasScreen(nav) }
        composable(Route.GenerarQR.path) { QrScreen() }
        composable(Route.Historial.path) { HistorialScreen() }
        composable(Route.Criterios.path) { CriteriosScreen() }

        // Ruta pública abierta desde el QR
        composable(
            route = Route.Evaluar.path,
            arguments = listOf(navArgument("mesaId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = Route.Evaluar.DeepLinkPattern })
        ) { backStack ->
            val mesaId = backStack.arguments?.getString("mesaId") ?: ""
            EvaluacionAnonimaScreen(mesaId = mesaId)
        }
    }
}

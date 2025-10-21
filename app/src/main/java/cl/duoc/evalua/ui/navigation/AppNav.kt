package cl.duoc.evalua.ui.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.evalua.data.datastore.SessionStore
import cl.duoc.evalua.ui.screens.*
import kotlinx.coroutines.flow.first

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val MESAS = "mesas"
    const val QR = "qr"
    const val HISTORIAL = "historial"
    const val CRITERIOS = "criterios"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.SPLASH
    ) {
        // Decide a qué pantalla ir según sesión guardada
        composable(Routes.SPLASH) {
            val app = LocalContext.current.applicationContext as Application
            val session = SessionStore(app)
            val loggedIn by session.isLoggedIn.collectAsState(initial = false)

            LaunchedEffect(loggedIn) {
                nav.navigate(if (loggedIn) Routes.HOME else Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        composable(Routes.LOGIN) {
            LoginScreen(onLoggedIn = {
                nav.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                onMesas = { nav.navigate(Routes.MESAS) },
                onQR = { nav.navigate(Routes.QR) },
                onHistorial = { nav.navigate(Routes.HISTORIAL) },
                onCriterios = { nav.navigate(Routes.CRITERIOS) },
                onLogout = {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MESAS) { MesasScreen() }
        composable(Routes.QR) { QrScreen() }
        composable(Routes.HISTORIAL) { HistorialScreen() }
        composable(Routes.CRITERIOS) { CriteriosScreen() }
    }
}

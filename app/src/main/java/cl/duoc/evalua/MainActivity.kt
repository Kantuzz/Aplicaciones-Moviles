package cl.duoc.evalua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import cl.duoc.evalua.core.ServiceLocator
import cl.duoc.evalua.ui.navigation.AppNavHost
import cl.duoc.evalua.ui.theme.EvaluacionGastroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(this)

        setContent {
            EvaluacionGastroTheme {
                val nav = rememberNavController()
                AppNavHost(nav)
            }
        }
    }
}

package br.com.opendriver

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import br.com.opendriver.service.CherryPickerService
import br.com.opendriver.service.FloatingButtonService
import br.com.opendriver.service.LocationTrackerService
import br.com.opendriver.ui.navigation.AppNavGraph
import br.com.opendriver.ui.theme.OpenDriverTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenDriverTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    onCopilotToggle = { active ->
                        toggleCopilot(active)
                    },
                    onTrackerToggle = { active ->
                        toggleTracker(active)
                    }
                )
            }
        }
    }

    private fun toggleCopilot(active: Boolean) {
        // Se ativando, garante a permissão de desenhar sobre outros apps (Overlay)
        if (active) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                return
            }

            // Inicia o FloatingButtonService que desenhará o botão flutuante e chamará o Copiloto
            val intent = Intent(this, FloatingButtonService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } else {
            stopService(Intent(this, FloatingButtonService::class.java))
            // Garante que o serviço de acessibilidade/copiloto também pare se necessário
            stopService(Intent(this, CherryPickerService::class.java))
        }
    }

    private fun toggleTracker(active: Boolean) {
        val intent = Intent(this, LocationTrackerService::class.java)
        if (active) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } else {
            stopService(intent)
        }
    }
}

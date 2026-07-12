package br.com.opendriver.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.opendriver.ui.dashboard.DashboardScreen
import br.com.opendriver.ui.history.HistoryScreen
import br.com.opendriver.ui.settings.SettingsScreen

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object History : Screen("history", "Histórico")
    object Settings : Screen("settings", "Ajustes")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onCopilotToggle: (Boolean) -> Unit,
    onTrackerToggle: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onCopilotToggle = onCopilotToggle,
                onTrackerToggle = onTrackerToggle,
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

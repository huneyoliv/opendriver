package br.com.opendriver.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.opendriver.ui.theme.BackgroundDark
import br.com.opendriver.ui.theme.GreenBrand
import br.com.opendriver.ui.theme.SurfaceDark
import br.com.opendriver.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onCopilotToggle: (Boolean) -> Unit,
    onTrackerToggle: (Boolean) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OpenDriver", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Principal de Ganhos do Dia
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("GANHOS DO DIA", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "R$ %.2f".format(uiState.dayEarnings),
                        color = GreenBrand,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${uiState.totalTripsToday} corridas registradas",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Cards Secundários (Km e Lucro por Km)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("KM DO DIA", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("%.1f km".format(uiState.dayDistanceKm), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("LUCRO LÍQUIDO/KM", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("R$ %.2f".format(uiState.netProfitPerKm), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seção de Controles (Switches)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Copiloto (Leitura de Tela)", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Detecta e avalia corridas automaticamente", color = TextSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = uiState.copilotActive,
                            onCheckedChange = { active ->
                                onCopilotToggle(active)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = GreenBrand)
                        )
                    }

                    Divider(color = BackgroundDark)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Rastreamento de GPS", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Calcula quilometragem real local", color = TextSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = uiState.trackerActive,
                            onCheckedChange = { active ->
                                viewModel.setTrackerActive(active)
                                onTrackerToggle(active)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = GreenBrand)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Atalhos de Navegação (Botões)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark)
                ) {
                    Text("Histórico", color = Color.White)
                }

                Button(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark)
                ) {
                    Text("Configurações", color = Color.White)
                }
            }
        }
    }
}

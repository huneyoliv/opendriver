package br.com.opendriver.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.opendriver.domain.model.TripOffer
import br.com.opendriver.domain.model.TripGrade
import br.com.opendriver.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Corridas", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                actions = {
                    if (uiState.trips.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAll() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Limpar tudo", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        if (uiState.trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma corrida registrada hoje.",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.trips, key = { it.id }) { trip ->
                    TripOfferItem(
                        trip = trip,
                        onDeleteClick = { viewModel.deleteTrip(trip.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TripOfferItem(
    trip: TripOffer,
    onDeleteClick: () -> Unit
) {
    val gradeColor = when (trip.finalGrade) {
        TripGrade.GREEN -> GreenGrade
        TripGrade.YELLOW -> YellowGrade
        TripGrade.RED -> RedGrade
        else -> TextSecondary
    }

    val platformName = when (trip.platform) {
        "com.ubercab.driver" -> "Uber"
        "com.app99.driver" -> "99"
        else -> "Outro"
    }

    val timeString = remember(trip.capturedAt) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(trip.capturedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$platformName • $timeString",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                // Chip de Nota
                Surface(
                    color = gradeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = trip.finalGrade?.name ?: "UNKNOWN",
                        color = gradeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "R$ %.2f".format(trip.valueRS),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "%.1f km • %d min".format(trip.distanceKm, trip.estimatedMinutes),
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deletar registro",
                        tint = Color.Red.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = BackgroundDark, thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${trip.origin} ➔ ${trip.destination}",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2
            )
        }
    }
}

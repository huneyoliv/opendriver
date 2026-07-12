package br.com.opendriver.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.opendriver.ui.theme.BackgroundDark
import br.com.opendriver.ui.theme.GreenBrand
import br.com.opendriver.ui.theme.SurfaceDark
import br.com.opendriver.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var costPerKm by remember { mutableStateOf("") }
    var greenPerKm by remember { mutableStateOf("") }
    var redPerKm by remember { mutableStateOf("") }
    var greenPerHour by remember { mutableStateOf("") }
    var redPerHour by remember { mutableStateOf("") }

    var notifyVoice by remember { mutableStateOf(true) }
    var notifyText by remember { mutableStateOf(true) }
    var saveOfferPrints by remember { mutableStateOf(false) }

    // Sincroniza estado quando carrega do DataStore
    LaunchedEffect(uiState.settings) {
        val s = uiState.settings
        costPerKm = s.costPerKm.toString()
        greenPerKm = s.greenPerKm.toString()
        redPerKm = s.redPerKm.toString()
        greenPerHour = s.greenPerHour.toString()
        redPerHour = s.redPerHour.toString()
        notifyVoice = s.notifyVoice
        notifyText = s.notifyText
        saveOfferPrints = s.saveOfferPrints
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Custos do veículo
            Text("Parâmetros do Veículo", color = GreenBrand, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = costPerKm,
                        onValueChange = { costPerKm = it },
                        label = { Text("Custo por Km Rodado (R$)", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            }

            // Thresholds de Lucratividade
            Text("Critérios de Lucro", color = GreenBrand, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = greenPerKm,
                            onValueChange = { greenPerKm = it },
                            label = { Text("Km Excelente (>= R$)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = redPerKm,
                            onValueChange = { redPerKm = it },
                            label = { Text("Km Ruim (< R$)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = greenPerHour,
                            onValueChange = { greenPerHour = it },
                            label = { Text("Hora Excelente (>= R$)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = redPerHour,
                            onValueChange = { redPerHour = it },
                            label = { Text("Hora Ruim (< R$)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }
                }
            }

            // Preferências do Overlay e TTS
            Text("Preferências de Alerta", color = GreenBrand, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Alerta de Voz (Text-to-Speech)", color = Color.White)
                        Switch(checked = notifyVoice, onCheckedChange = { notifyVoice = it }, colors = SwitchDefaults.colors(checkedThumbColor = GreenBrand))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Exibir Notificação Local", color = Color.White)
                        Switch(checked = notifyText, onCheckedChange = { notifyText = it }, colors = SwitchDefaults.colors(checkedThumbColor = GreenBrand))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Salvar Prints de Ofertas", color = Color.White)
                        Switch(checked = saveOfferPrints, onCheckedChange = { saveOfferPrints = it }, colors = SwitchDefaults.colors(checkedThumbColor = GreenBrand))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Salvar
            Button(
                onClick = {
                    val currentSettings = uiState.settings
                    val updated = currentSettings.copy(
                        costPerKm = costPerKm.toFloatOrNull() ?: currentSettings.costPerKm,
                        greenPerKm = greenPerKm.toFloatOrNull() ?: currentSettings.greenPerKm,
                        redPerKm = redPerKm.toFloatOrNull() ?: currentSettings.redPerKm,
                        greenPerHour = greenPerHour.toFloatOrNull() ?: currentSettings.greenPerHour,
                        redPerHour = redPerHour.toFloatOrNull() ?: currentSettings.redPerHour,
                        notifyVoice = notifyVoice,
                        notifyText = notifyText,
                        saveOfferPrints = saveOfferPrints
                    )
                    viewModel.updateSettings(updated)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenBrand)
            ) {
                Text("SALVAR ALTERAÇÕES", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

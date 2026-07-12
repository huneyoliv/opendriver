package br.com.opendriver.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import br.com.opendriver.data.parser.OfferParser
import br.com.opendriver.domain.repository.TripRepository
import br.com.opendriver.domain.repository.SettingsRepository
import br.com.opendriver.domain.usecase.GradeOfferUseCase
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class CherryPickerService : AccessibilityService() {

    private val tripRepository: TripRepository by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val gradeOfferUseCase: GradeOfferUseCase by inject()

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var overlayCardManager: OverlayCardManager? = null

    override fun onCreate() {
        super.onCreate()
        overlayCardManager = OverlayCardManager(this, settingsRepository)

        // Observa eventos alternativos de Notificação e Captura de Tela (OCR)
        observeNotificationChannel()
        observeOcrChannel()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Opcional: Configurações adicionais de acessibilidade via código, se necessário
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val ev = event ?: return
        val packageName = ev.packageName?.toString() ?: return

        // Processa apenas pacotes suportados de transporte
        if (packageName !in OfferParser.SUPPORTED_PACKAGES) return

        // 1. Extrai o texto da janela ativa via acessibilidade
        val rootNode = rootInActiveWindow ?: return
        val nodesTextList = mutableListOf<String>()
        collectNodesText(rootNode, nodesTextList)
        val windowText = nodesTextList.joinToString("\n").trim()

        if (windowText.isNotBlank()) {
            processRawText(windowText, packageName)
        }
    }

    private fun collectNodesText(node: android.view.accessibility.AccessibilityNodeInfo?, list: MutableList<String>) {
        val n = node ?: return
        n.text?.toString()?.trim()?.let {
            if (it.isNotBlank()) list.add(it)
        }
        for (i in 0 until n.childCount) {
            collectNodesText(n.getChild(i), list)
        }
    }

    private fun observeNotificationChannel() {
        serviceScope.launch {
            RideNotificationListener.notificationTextFlow.collect { data ->
                processRawText(data.text, data.packageName)
            }
        }
    }

    private fun observeOcrChannel() {
        serviceScope.launch {
            ScreenCaptureService.ocrTextFlow.collect { text ->
                // Usa o pacote ativo atual do emulador/dispositivo
                val activePkg = rootInActiveWindow?.packageName?.toString() ?: "com.ubercab.driver"
                processRawText(text, activePkg)
            }
        }
    }

    private fun processRawText(rawText: String, packageName: String) {
        val offer = OfferParser.parse(rawText, packageName) ?: return

        serviceScope.launch {
            // Evita processar a mesma corrida repetidamente em curto intervalo (de-bounce)
            val gradeResult = gradeOfferUseCase(offer)

            // Salva no banco de dados local
            tripRepository.save(offer, gradeResult)

            // Exibe o card de overlay na tela
            overlayCardManager?.show(offer, gradeResult)
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        overlayCardManager?.destroy()
        overlayCardManager = null
    }
}

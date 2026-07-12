package br.com.opendriver.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import br.com.opendriver.domain.model.TripOffer
import br.com.opendriver.domain.model.TripGrade
import br.com.opendriver.domain.model.GradeResult
import br.com.opendriver.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class OverlayCardManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : TextToSpeech.OnInitListener {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private val handler = Handler(Looper.getMainLooper())
    private var dismissRunnable: Runnable? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                ttsReady = true
            }
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    fun show(offer: TripOffer, gradeResult: GradeResult) {
        // Remove overlay anterior se existir
        dismiss()

        CoroutineScope(Dispatchers.Main).launch {
            val settings = settingsRepository.observeSettings().first()

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            overlayView = inflater.inflate(br.com.opendriver.R.layout.overlay_card, null)

            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.BOTTOM
                y = 150
            }

            val gradeTv = overlayView?.findViewById<TextView>(br.com.opendriver.R.id.grade_tv)
            val valueTv = overlayView?.findViewById<TextView>(br.com.opendriver.R.id.value_tv)
            val profitKmTv = overlayView?.findViewById<TextView>(br.com.opendriver.R.id.profit_km_tv)
            val profitHourTv = overlayView?.findViewById<TextView>(br.com.opendriver.R.id.profit_hour_tv)
            val addressesTv = overlayView?.findViewById<TextView>(br.com.opendriver.R.id.addresses_tv)
            val acceptBtn = overlayView?.findViewById<Button>(br.com.opendriver.R.id.accept_btn)
            val rejectBtn = overlayView?.findViewById<Button>(br.com.opendriver.R.id.reject_btn)

            // Formatação do Card
            val gradeColor = when (gradeResult.finalGrade) {
                TripGrade.GREEN -> 0xFF4CAF50.toInt()
                TripGrade.YELLOW -> 0xFFFFC107.toInt()
                TripGrade.RED -> 0xFFF44336.toInt()
            }

            gradeTv?.setTextColor(gradeColor)
            gradeTv?.text = "CORRIDA ${gradeResult.finalGrade.name}"

            valueTv?.text = "R$ %.2f".format(offer.valueRS)
            profitKmTv?.text = "R$ %.2f/km".format(gradeResult.lucroPerKm)
            profitHourTv?.text = "R$ %.2f/h".format(gradeResult.lucroPerHour)
            addressesTv?.text = "${offer.origin} ➔ ${offer.destination}"

            // Transmitir áudio por TTS se ativado
            if (settings.notifyVoice && ttsReady) {
                val ttsText = when (gradeResult.finalGrade) {
                    TripGrade.GREEN -> "Corrida Excelente! Valor %.0f reais".format(offer.valueRS)
                    TripGrade.YELLOW -> "Corrida Regular. Valor %.0f reais".format(offer.valueRS)
                    TripGrade.RED -> "Corrida Ruim. Recusar."
                }
                tts?.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, "opendriver_tts")
            }

            acceptBtn?.setOnClickListener {
                // Simulação de clique reativa
                dismiss()
            }

            rejectBtn?.setOnClickListener {
                dismiss()
            }

            windowManager.addView(overlayView, params)

            // Auto dismiss após timeout configurado
            dismissRunnable = Runnable { dismiss() }
            handler.postDelayed(dismissRunnable!!, settings.overlayDuration.toLong())
        }
    }

    fun dismiss() {
        dismissRunnable?.let { handler.removeCallbacks(it) }
        dismissRunnable = null

        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                // Ignora se a view já foi removida
            }
            overlayView = null
        }
    }

    fun destroy() {
        dismiss()
        tts?.stop()
        tts?.shutdown()
    }
}

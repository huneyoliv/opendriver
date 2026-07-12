package br.com.opendriver.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FloatingButtonService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(
            NOTIFICATION_ID,
            createNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else 0
        )

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        setupFloatingButton()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingButton() {
        floatingView = LayoutInflater.from(this).inflate(br.com.opendriver.R.layout.floating_button, null)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        val collapsedIv = floatingView?.findViewById<ImageView>(br.com.opendriver.R.id.collapsed_iv)

        floatingView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val ev = event ?: return false
                val p = params ?: return false
                val view = floatingView ?: return false

                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = p.x
                        initialY = p.y
                        initialTouchX = ev.rawX
                        initialTouchY = ev.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val diffX = ev.rawX - initialTouchX
                        val diffY = ev.rawY - initialTouchY
                        if (diffX < 10 && diffY < 10) {
                            // Clicou no botão flutuante
                            toggleCopilotState(collapsedIv)
                        }
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        p.x = initialX + (ev.rawX - initialTouchX).toInt()
                        p.y = initialY + (ev.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(view, p)
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(floatingView, params)
    }

    private fun toggleCopilotState(collapsedIv: ImageView?) {
        val newState = !_copilotActiveFlow.value
        _copilotActiveFlow.value = newState

        if (newState) {
            collapsedIv?.setImageResource(android.R.drawable.ic_media_pause)
            // Disparar início da ScreenCaptureActivity para obter permissão e ligar OCR
            val captureIntent = Intent(this, ScreenCaptureActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(captureIntent)
        } else {
            collapsedIv?.setImageResource(android.R.drawable.ic_media_play)
            // Para o serviço de captura
            stopService(Intent(this, ScreenCaptureService::class.java))
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OpenDriver Flutuante")
            .setContentText("Atalho rápido de monitoramento ativo na tela.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Botão Flutuante",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { windowManager.removeView(it) }
    }

    companion object {
        private const val CHANNEL_ID = "floating_button_channel"
        private const val NOTIFICATION_ID = 2003

        private val _copilotActiveFlow = MutableStateFlow(false)
        val copilotActiveFlow = _copilotActiveFlow.asStateFlow()
    }
}

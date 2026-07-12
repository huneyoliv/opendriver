package br.com.opendriver.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import br.com.opendriver.data.parser.OfferParser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RideNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val sbnNonNull = sbn ?: return
        val pkg = sbnNonNull.packageName ?: return

        // Intercepta apenas se pertencer aos pacotes suportados de transporte
        if (pkg !in OfferParser.SUPPORTED_PACKAGES) return

        val extras = sbnNonNull.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

        val fullText = buildString {
            if (title.isNotBlank()) append(title).append("\n")
            if (text.isNotBlank()) append(text).append("\n")
            if (bigText.isNotBlank()) append(bigText)
        }.trim()

        if (fullText.isNotBlank()) {
            _notificationTextFlow.tryEmit(NotificationData(fullText, pkg))
        }
    }

    data class NotificationData(val text: String, val packageName: String)

    companion object {
        private val _notificationTextFlow = MutableSharedFlow<NotificationData>(extraBufferCapacity = 5)
        val notificationTextFlow = _notificationTextFlow.asSharedFlow()
    }
}

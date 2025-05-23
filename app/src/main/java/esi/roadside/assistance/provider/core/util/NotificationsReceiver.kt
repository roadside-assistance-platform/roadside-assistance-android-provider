package esi.roadside.assistance.provider.core.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.presentation.MainActivity

class NotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val service = NotificationService(context)

        // Extract notification details from intent
        val title = intent.getStringExtra("title") ?: "New Request"
        val message = intent.getStringExtra("message") ?: "You have a new service request"
        val serviceId = intent.getStringExtra("serviceId")
        val notificationId = intent.getIntExtra("notificationId", System.currentTimeMillis().toInt())

        // Create actions for the notification
        val actions = mutableListOf<NotificationCompat.Action>()

        if (serviceId != null) {
            // Create Accept action
            val acceptIntent = Intent(context, MainActivity::class.java).apply {
                putExtra("serviceId", serviceId)
                putExtra("action", "accept")
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val acceptPendingIntent = PendingIntent.getActivity(
                context,
                0,
                acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val acceptAction = NotificationCompat.Action.Builder(
                R.drawable.baseline_check_24, // Make sure this icon exists in your resources
                "Accept",
                acceptPendingIntent
            ).build()

            // Create View action
            val viewIntent = Intent(context, MainActivity::class.java).apply {
                putExtra("serviceId", serviceId)
                putExtra("action", "view")
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val viewPendingIntent = PendingIntent.getActivity(
                context,
                1,
                viewIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val viewAction = NotificationCompat.Action.Builder(
                null, // Make sure this icon exists in your resources
                "View",
                viewPendingIntent
            ).build()

            actions.add(acceptAction)
            actions.add(viewAction)
        }

        // Create extras map for the notification
        val extras = mapOf(
            "serviceId" to (serviceId ?: ""),
            "fromNotification" to true
        )

        // Show notification
        service.showNotification(
            id = notificationId,
            title = title,
            content = message,
            extras = extras,
            actions = actions.toTypedArray()
        )
    }
}
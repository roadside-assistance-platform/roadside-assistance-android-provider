package esi.roadside.assistance.provider

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import esi.roadside.assistance.provider.core.util.NotificationsReceiver

class NotificationService(
    private val context: Context,

) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun showNotification(
        title: String,
        content: String,
    ) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = intent?.let {
            androidx.core.app.TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(it)
                .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(
                context,
                NotificationsReceiver::class.java
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID,
        )
            .setContentTitle(CHANNEL_NAME)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_telegram_app,
                context.getString(R.string.accept),
                acceptPendingIntent,
            )
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    companion object {
        const val CHANNEL_ID = "esi.roadside.assistance.provider"
        const val CHANNEL_NAME = "Roadside Assistance Provider"
        const val CHANNEL_DESCRIPTION = "Roadside Assistance Provider Notifications"
        const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
        const val NOTIFICATION_ID = 1
    }
}
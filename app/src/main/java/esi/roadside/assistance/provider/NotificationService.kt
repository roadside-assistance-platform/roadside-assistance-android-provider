package esi.roadside.assistance.provider

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder

class NotificationService(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun getPendingIntent(intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun showNotification(
        id: Int,
        title: String,
        content: String,
        extras: Map<String, String> = emptyMap(),
        vararg actions: NotificationCompat.Action
    ) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName).apply {
            extras.forEach {
                this?.putExtra(it.key, it.value)
            }
            this?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = intent?.let {
            TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(it)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID,
        )
            .setContentTitle(CHANNEL_NAME)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
                    .setBigContentTitle(title)
                    .setSummaryText(content)
            )
            .apply {
                actions.forEach { addAction(it) }
            }
            .build()
        notificationManager.notify(id, notification)
    }
    companion object {
        const val CHANNEL_ID = "esi.roadside.assistance.provider"
        const val CHANNEL_NAME = "Roadside Assistance Provider"
        const val CHANNEL_DESCRIPTION = "Roadside Assistance Provider Notifications"
        const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }
}
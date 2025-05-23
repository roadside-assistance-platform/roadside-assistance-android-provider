package esi.roadside.assistance.provider

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import esi.roadside.assistance.provider.main.presentation.MainActivity


class NotificationService(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, CHANNEL_IMPORTANCE)
            .setName(CHANNEL_NAME)
            .setDescription(CHANNEL_DESCRIPTION)
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }


    /**
     * Creates a PendingIntent for the app's main activity.
     *
     * @param apply A lambda function to modify the intent before creating the PendingIntent.
     * @return A PendingIntent that can be used to launch the app's main activity.
     */
    fun getPendingIntent(apply: (Intent) -> Intent): PendingIntent? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return intent?.let {
            PendingIntent.getActivity(
                context,
                0,
                apply(it),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    /**
     * Shows a notification with the given parameters.
     *
     * @param id The unique ID for the notification.
     * @param title The title of the notification.
     * @param content The content text of the notification.
     * @param extras Optional extras to pass to the intent.
     * @param actions Optional actions to add to the notification.
     */
    fun showNotification(
        id: Int,
        title: String,
        content: String,
        extras: Map<String, Any>? = null,
        vararg actions: NotificationCompat.Action,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            extras?.forEach { (key, value) ->
                when (value) {
                    is Boolean -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is String -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Double -> putExtra(key, value)
                    is Char -> putExtra(key, value)
                    is Short -> putExtra(key, value)
                    is Byte -> putExtra(key, value)
                    else -> putExtra(key, value.toString())
                }
            }
        }
        val pendingIntent = intent.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(CHANNEL_NAME)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
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

    /**
     * Shows a notification with the given parameters, using a string resource for the title.
     *
     * @param id The unique ID for the notification.
     * @param title The resource ID of the title string.
     * @param content The content text of the notification.
     * @param extras Optional extras to pass to the intent.
     * @param actions Optional actions to add to the notification.
     */
    fun showNotification(
        id: Int,
        title: Int,
        content: String,
        extras: Map<String, Any>? = null,
        vararg actions: NotificationCompat.Action,
    ) = showNotification(id, context.getString(title), content, extras, *actions)

    companion object {
        const val CHANNEL_ID = "esi.roadside.assistance.client"
        const val CHANNEL_NAME = "Roadside Assistance Client"
        const val CHANNEL_DESCRIPTION = "Roadside Assistance Client Notifications"
        const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }
}

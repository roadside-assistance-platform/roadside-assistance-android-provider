package esi.roadside.assistance.provider.main.util

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.util.NotificationsReceiver
import esi.roadside.assistance.provider.main.domain.models.NotificationServiceModel

class NotificationManager(
    private val notificationService: NotificationService,
    private val context: Context,
) {
    fun showServiceNotification(serviceModel: NotificationServiceModel, serviceCount: Int) {
        val sb = StringBuilder()
        if (serviceModel.serviceLocationString.isNotEmpty())
            sb.append("Location: ${serviceModel.serviceLocationString}.\n")
        sb.append("Category: ${context.getString(serviceModel.category.text)}.\n")
        notificationService.showNotification(
            serviceCount,
            "New service request from ${serviceModel.client.fullName}.\n",
            sb.toString(),
            mapOf("serviceId" to serviceModel.id),
            NotificationCompat.Action(
                R.drawable.baseline_check_24,
                context.getString(R.string.accept),
                notificationService.getPendingIntent(
                    Intent(context, NotificationsReceiver::class.java).apply {
                        putExtra("serviceId", serviceModel.id)
                        putExtra("action", "accept")
                    }
                )
            )
        )
    }
}
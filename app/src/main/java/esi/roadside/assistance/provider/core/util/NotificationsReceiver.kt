package esi.roadside.assistance.provider.core.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import esi.roadside.assistance.provider.NotificationService

class NotificationsReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val service = NotificationService(context!!)

    }
}
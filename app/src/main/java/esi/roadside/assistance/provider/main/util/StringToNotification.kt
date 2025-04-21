package esi.roadside.assistance.provider.main.util

import esi.roadside.assistance.provider.main.domain.models.NotificationModel
import java.time.LocalDateTime

fun String.toNotificationModel(createdAt: LocalDateTime): NotificationModel {
    return NotificationModel("", "", "", false, "", createdAt)
}
package esi.roadside.assistance.provider.main.data.dto

import esi.roadside.assistance.provider.main.domain.models.NotificationModel
import java.time.LocalDateTime

data class Notification(
    val id: String,
    val title: String,
    val text: String,
    val isWarning: Boolean,
    val image: String?,
    val createdAt: LocalDateTime
) {
    fun toNotificationModel(): NotificationModel =
        NotificationModel(id, title, text, isWarning, image, createdAt)
}

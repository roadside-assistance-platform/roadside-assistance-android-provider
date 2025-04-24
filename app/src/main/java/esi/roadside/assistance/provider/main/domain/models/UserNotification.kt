package esi.roadside.assistance.provider.main.domain.models

import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class UserNotification(
    val id: String,
    val title: String,
    val text: String,
    val isWarning: Boolean,
    val image: String?,
    val createdAt: String
) {
    fun toUserNotificationModel() = UserNotificationModel(
        id,
        title,
        text,
        isWarning,
        image,
        try {
            OffsetDateTime.parse(createdAt)
        } catch (_: Exception) {
            OffsetDateTime.now()
        }.toLocalDateTime().atZone(ZoneId.systemDefault())
    )
}
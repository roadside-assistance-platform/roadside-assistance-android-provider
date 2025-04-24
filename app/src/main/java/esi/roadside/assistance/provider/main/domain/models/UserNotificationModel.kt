package esi.roadside.assistance.provider.main.domain.models

import java.time.ZonedDateTime

data class UserNotificationModel(
    val id: String,
    val title: String,
    val text: String,
    val isWarning: Boolean = false,
    val image: String?,
    val createdAt: ZonedDateTime
)

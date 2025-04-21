package esi.roadside.assistance.provider.main.domain.models

import java.time.LocalDateTime

data class NotificationModel(
    val id: String,
    val title: String,
    val text: String,
    val isWarning: Boolean,
    val image: String?,
    val createdAt: LocalDateTime
)

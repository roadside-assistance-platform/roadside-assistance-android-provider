package esi.roadside.assistance.provider.main.data.dto

import esi.roadside.assistance.provider.main.domain.models.NotificationType
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val type: NotificationType,
    val data: String
)

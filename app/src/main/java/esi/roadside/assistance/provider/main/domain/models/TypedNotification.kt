package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.core.data.dto.Service
import kotlinx.serialization.Serializable

interface NotificationData

enum class NotificationType(val clazz: Class<*>) {
    SERVICE(Service.javaClass),
    USER_NOTIFICATION(UserNotification.javaClass),
    LOCATION_UPDATE(LocationModel.javaClass),
    ASSISTANCE_REQUEST(LocationModel.javaClass),
}

@Serializable
data class TypedNotification<T>(
    val type: NotificationType,
    val data: T
)
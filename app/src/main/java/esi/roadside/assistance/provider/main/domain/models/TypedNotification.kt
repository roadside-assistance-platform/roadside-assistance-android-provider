package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.core.data.dto.Service

interface NotificationData

enum class NotificationType(val clazz: Class<*>) {
    SERVICE(Service.javaClass), USER_NOTIFICATION(UserNotification.javaClass)
}

data class TypedNotification<T>(
    val type: NotificationType,
    val data: T
)
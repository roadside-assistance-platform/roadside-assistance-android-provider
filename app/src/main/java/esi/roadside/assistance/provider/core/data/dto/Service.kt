package esi.roadside.assistance.provider.core.data.dto

import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationData
import esi.roadside.assistance.provider.main.domain.models.ServiceModel
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class Service(
    val id: String = "",
    val clientId: String = "",
    val providerId: String? = null,
    val price: Int = 0,
    val description: String? = "",
    val serviceRating: Float = 0f,
    val serviceLocation: String = "",
    val done: Boolean = false,
    val serviceCategory: Categories = Categories.OTHER,
    val createdAt: String = "",
    val updatedAt: String = "",
    val comments: List<Comment> = emptyList()
): NotificationData {
    fun toServiceModel(locationString: String) = ServiceModel(
        id = id,
        clientId = clientId,
        providerId = providerId,
        price = price,
        serviceRating = serviceRating,
        serviceLocation = LocationModel.fromString(serviceLocation),
        serviceLocationString = locationString,
        done = done,
        category = serviceCategory,
        createdAt = ZonedDateTime.parse(createdAt),
        updatedAt = ZonedDateTime.parse(updatedAt),
        comments = comments.map { it.toCommentModel() },
        description = description
    )
}
package esi.roadside.assistance.provider.core.data.dto

import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationData
import esi.roadside.assistance.provider.main.domain.models.ServiceModel
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import kotlin.String

@Serializable
data class Service(
    val id: String = "",
    val clientId: String = "",
    val providerId: String? = null,
    val price: Int = 0,
    val description: String = "",
    val serviceRating: Float = 0f,
    val serviceLocation: String = "",
    val done: Boolean = false,
    val serviceCategory: Categories = Categories.OTHER,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
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
        createdAt = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()),
        updatedAt = Instant.ofEpochMilli(updatedAt).atZone(ZoneId.systemDefault()),
        comments = comments.map { it.toCommentModel() },
        description = description
    )
}
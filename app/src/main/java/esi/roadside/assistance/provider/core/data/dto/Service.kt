package esi.roadside.assistance.provider.core.data.dto

import esi.roadside.assistance.provider.core.domain.Category
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationData
import esi.roadside.assistance.provider.main.domain.models.ServiceModel
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import kotlin.String

@Serializable
data class Service(
    val id: String,
    val clientId: String,
    val providerId: String,
    val price: Int,
    val serviceRating: Int,
    val serviceLocation: String,
    val done: Boolean,
    val category: Categories,
    val createdAt: Long,
    val updatedAt: Long,
    val comments: List<Comment>
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
        category = category,
        createdAt = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()),
        updatedAt = Instant.ofEpochMilli(updatedAt).atZone(ZoneId.systemDefault()),
        comments = comments.map { it.toCommentModel() }
    )
}
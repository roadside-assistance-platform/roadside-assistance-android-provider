package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.core.domain.Category
import esi.roadside.assistance.provider.core.data.dto.Service
import esi.roadside.assistance.provider.main.domain.Categories
import java.time.ZonedDateTime
import java.util.Date

data class ServiceModel(
    val id: String,
    val clientId: String,
    val providerId: String,
    val price: Int,
    val serviceRating: Int,
    val serviceLocation: LocationModel,
    val serviceLocationString: String = "",
    val done: Boolean,
    val category: Categories,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val comments: List<CommentModel>
) {
    fun toService() = Service(
        id = id,
        clientId = clientId,
        providerId = providerId,
        price = price,
        serviceRating = serviceRating,
        serviceLocation = serviceLocation.toString(),
        done = done,
        category = category,
        createdAt = createdAt.toInstant().toEpochMilli(),
        updatedAt = updatedAt.toInstant().toEpochMilli(),
        comments = comments.map { it.toComment() }
    )
}

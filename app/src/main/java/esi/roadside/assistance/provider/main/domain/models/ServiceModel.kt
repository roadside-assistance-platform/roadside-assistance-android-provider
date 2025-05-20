package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.core.data.dto.Service
import esi.roadside.assistance.provider.main.data.dto.JsonDirectionsResponse
import esi.roadside.assistance.provider.main.domain.Categories
import java.time.ZonedDateTime

data class ServiceModel(
    val id: String,
    val clientId: String,
    val providerId: String?,
    val price: Int,
    val description: String?,
    val serviceRating: Float,
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
        serviceCategory = category,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        comments = comments.map { it.toComment() },
        description = description
    )
}

data class ServiceInfo(
    val id: String,
    val client: ClientInfo,
    val providerId: String?,
    val price: Int,
    val description: String,
    val serviceRating: Float,
    val serviceLocation: LocationModel,
    val serviceLocationString: String = "",
    val directions: JsonDirectionsResponse = JsonDirectionsResponse("", emptyList(), "", emptyList()),
    val done: Boolean,
    val category: Categories,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val comments: List<CommentModel>
)
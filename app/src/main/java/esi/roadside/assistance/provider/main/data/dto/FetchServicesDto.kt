package esi.roadside.assistance.provider.main.data.dto

import esi.roadside.assistance.provider.core.data.dto.Service
import esi.roadside.assistance.provider.main.domain.models.ClientInfo
import esi.roadside.assistance.provider.main.domain.models.FetchServicesDataModel
import esi.roadside.assistance.provider.main.domain.models.ProviderInfo
import kotlinx.serialization.Serializable

@Serializable
data class FetchServicesDto(
    val status: String,
    val data: FetchServicesData
)

@Serializable
data class FetchServicesData(
    val services: List<Service>,
    val provider: ProviderInfo,
    val statistics: Statistics
) {
    suspend fun toDomainModel(callback: suspend (Service) -> String) = FetchServicesDataModel(
        services = services.map { it.toServiceModel(callback(it)) },
        provider = provider,
        statistics = statistics
    )
}

@Serializable
data class Statistics(
    val totalServices: Int = 0,
    val completedServices: Int = 0,
    val pendingServices: Int = 0,
    val averageRating: Double? = 0.0,
    val totalRatings: Int = 0
)
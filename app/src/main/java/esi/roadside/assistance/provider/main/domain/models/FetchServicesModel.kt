package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.main.data.dto.Statistics

data class FetchServicesModel(
    val status: String = "",
    val data: FetchServicesDataModel = FetchServicesDataModel()
)

data class FetchServicesDataModel(
    val services: List<ServiceModel> = emptyList(),
    val provider: ProviderInfo = ProviderInfo(),
    val statistics: Statistics = Statistics()
)

package esi.roadside.assistance.provider.main.domain.repository

import esi.roadside.assistance.provider.main.domain.models.ProviderInfo
import esi.roadside.assistance.provider.main.domain.models.ServiceInfo
import esi.roadside.assistance.provider.main.presentation.routes.home.ProviderState

data class ServiceState(
    val services: List<ServiceInfo> = emptyList(),
    val selected: Int? = null,
    val providerState: ProviderState = ProviderState.IDLE,
    val serviceModel: ServiceInfo? = null,
    val clientInfo: ProviderInfo? = null,
    val price: Int = 0,
    val rating: Double? = null
)
package esi.roadside.assistance.provider.main.presentation.routes.home

import com.mapbox.geojson.Point
import esi.roadside.assistance.provider.main.data.dto.JsonDirectionsResponse
import esi.roadside.assistance.provider.main.data.dto.RouteX
import esi.roadside.assistance.provider.main.domain.models.NotificationServiceModel
import esi.roadside.assistance.provider.main.domain.models.ServiceModel
import esi.roadside.assistance.provider.main.presentation.models.ClientUi

enum class ProviderState {
    IDLE,
    NAVIGATING,
    WORKING,
    COMPLETED,
}

data class HomeUiState(
    val services: List<NotificationServiceModel> = emptyList(),
    val providerState: ProviderState = ProviderState.IDLE,
    val clientDialog: Boolean = true,
    val selectedService: Int? = null,
    val location: Point? = null,
    val directions: RouteX? = null,
    val loading: Boolean = false,
    val price: Int = 0,
    val rating: Double? = null
)
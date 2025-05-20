package esi.roadside.assistance.provider.main.presentation.routes.home

import com.mapbox.geojson.Point
import esi.roadside.assistance.provider.main.data.dto.RouteX
import esi.roadside.assistance.provider.main.domain.models.ServiceInfo

enum class ProviderState {
    IDLE,
    NAVIGATING,
    WORKING,
    COMPLETED,
}

data class HomeUiState(
    val clientDialog: Boolean = true,
    val location: Point? = null,
    val directions: RouteX? = null,
    val loading: Boolean = false,
    val rating: Double? = null
)
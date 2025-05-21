package esi.roadside.assistance.provider.main.presentation.routes.home

import esi.roadside.assistance.provider.R
import com.mapbox.geojson.Point
import esi.roadside.assistance.provider.main.data.dto.RouteX

enum class ProviderState(val title: Int, val text: Int) {
    IDLE(R.string.idle, R.string.idle),
    NAVIGATING(R.string.navigating, R.string.navigating_text),
    WORKING(R.string.working, R.string.working_description),
    COMPLETED(R.string.completed, R.string.completed),
}

data class HomeUiState(
    val clientDialog: Boolean = true,
    val location: Point? = null,
    val directions: RouteX? = null,
    val loading: Boolean = false,
    val rating: Double? = null,
    val message: String = "",
)
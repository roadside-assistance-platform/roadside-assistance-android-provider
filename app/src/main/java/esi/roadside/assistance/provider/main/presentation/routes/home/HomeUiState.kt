package esi.roadside.assistance.provider.main.presentation.routes.home

import com.mapbox.geojson.Point

data class HomeUiState(
    val location: Point? = null,
)
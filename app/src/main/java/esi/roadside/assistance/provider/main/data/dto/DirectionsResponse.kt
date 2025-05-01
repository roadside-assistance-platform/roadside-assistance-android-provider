package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DirectionsResponse(
    val routes: List<Route>,
)

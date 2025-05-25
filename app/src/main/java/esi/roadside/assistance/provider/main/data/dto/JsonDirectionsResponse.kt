package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class JsonDirectionsResponse(
    val code: String = "",
    val routes: List<RouteX> = emptyList(),
    val uuid: String = "",
    val waypoints: List<Waypoint> = emptyList()
)
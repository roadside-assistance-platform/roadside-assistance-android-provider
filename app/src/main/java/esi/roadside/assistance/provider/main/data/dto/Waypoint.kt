package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Waypoint(
    val distance: Double,
    val location: List<Double>,
    val name: String
)
package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RouteX(
    val distance: Double,
    val duration: Double,
    val geometry: Geometry,
    val legs: List<Leg>,
    val weight: Double,
    val weight_name: String
)
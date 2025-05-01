package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val distance: Double,
    val driving_side: String,
    val duration: Double,
    val geometry: Geometry,
    val intersections: List<Intersection>,
    val maneuver: Maneuver,
    val mode: String,
    val name: String,
    val rotary_name: String,
    val weight: Double
)
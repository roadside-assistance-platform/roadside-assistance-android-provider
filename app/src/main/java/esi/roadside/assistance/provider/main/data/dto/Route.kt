package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val distance: Double,
    val duration: Double,
    val geometry: String
)

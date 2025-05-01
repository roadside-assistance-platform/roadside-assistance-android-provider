package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Maneuver(
    val bearing_after: Int,
    val bearing_before: Int,
    val exit: Int,
    val instruction: String,
    val location: List<Double>,
    val modifier: String,
    val type: String
)
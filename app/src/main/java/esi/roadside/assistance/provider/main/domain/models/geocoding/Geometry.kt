package esi.roadside.assistance.provider.main.domain.models.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val coordinates: List<Double>,
    val type: String
)
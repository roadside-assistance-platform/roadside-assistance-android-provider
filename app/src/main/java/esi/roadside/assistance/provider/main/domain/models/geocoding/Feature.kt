package esi.roadside.assistance.provider.main.domain.models.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Feature(
    val geometry: Geometry,
    val id: String,
    val properties: Properties,
    val type: String
)
package esi.roadside.assistance.provider.main.domain.models.geocoding

data class Feature(
    val geometry: Geometry,
    val id: String,
    val properties: Properties,
    val type: String
)
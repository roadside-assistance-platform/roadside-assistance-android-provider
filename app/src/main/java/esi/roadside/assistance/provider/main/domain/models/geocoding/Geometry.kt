package esi.roadside.assistance.provider.main.domain.models.geocoding

data class Geometry(
    val coordinates: List<Double>,
    val type: String
)
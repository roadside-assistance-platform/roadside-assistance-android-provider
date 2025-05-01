package esi.roadside.assistance.provider.main.domain.models.geocoding

data class ReverseGeocodingResponse(
    val attribution: String,
    val features: List<Feature>,
    val type: String
)
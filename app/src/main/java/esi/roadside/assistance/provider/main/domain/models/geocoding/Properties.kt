package esi.roadside.assistance.provider.main.domain.models.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    val bbox: List<Double>,
    val context: Context,
    val coordinates: Coordinates,
    val feature_type: String,
    val full_address: String,
    val mapbox_id: String,
    val name: String,
    val name_preferred: String,
    val place_formatted: String
)
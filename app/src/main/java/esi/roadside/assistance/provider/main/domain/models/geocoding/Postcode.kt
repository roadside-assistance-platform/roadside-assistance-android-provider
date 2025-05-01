package esi.roadside.assistance.provider.main.domain.models.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Postcode(
    val mapbox_id: String,
    val name: String
)
package esi.roadside.assistance.provider.main.domain.models.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val mapbox_id: String,
    val name: String,
    val wikidata_id: String
)
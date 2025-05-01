package esi.roadside.assistance.provider.main.domain.models.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Context(
    val country: Country,
    val place: Place,
    val postcode: Postcode,
    val region: Region
)
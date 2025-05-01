package esi.roadside.assistance.provider.main.domain.models.geocoding

data class Context(
    val country: Country,
    val place: Place,
    val postcode: Postcode,
    val region: Region
)
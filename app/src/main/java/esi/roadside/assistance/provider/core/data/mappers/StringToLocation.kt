package esi.roadside.assistance.provider.core.data.mappers

import esi.roadside.assistance.provider.main.domain.models.LocationModel

fun String.toLocation(): LocationModel {
    val regex = Regex("^-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?$")
    require(regex.matches(this)) { "Invalid location format" }
    val location = this.split(",")
    return LocationModel(
        latitude = location[0].toDouble(),
        longitude = location[1].toDouble()
    )
}
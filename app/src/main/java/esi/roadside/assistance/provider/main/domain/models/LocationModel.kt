package esi.roadside.assistance.provider.main.domain.models

import com.mapbox.geojson.Point
import esi.roadside.assistance.provider.core.data.mappers.toLocation
import kotlinx.serialization.Serializable

@Serializable
data class LocationModel(
    val longitude: Double,
    val latitude: Double,
): NotificationData {
    companion object {
        fun fromString(location: String) = location.toLocation()
    }
    override fun toString() = "${longitude},${latitude}"
    fun toPoint(): Point = Point.fromLngLat(longitude, latitude)
}

fun Point.toLocationModel() = LocationModel(
    longitude = longitude(),
    latitude = latitude(),
)

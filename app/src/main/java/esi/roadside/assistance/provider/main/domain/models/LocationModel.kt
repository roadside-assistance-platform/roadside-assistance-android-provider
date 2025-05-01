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
        fun fromPoint(point: Point) = LocationModel(
            longitude = point.longitude(),
            latitude = point.latitude(),
        )
    }

    override fun toString() = "${longitude},${latitude}"

    fun toPoint() = Point.fromLngLat(longitude, latitude)
}

fun Point.toLocationModel() = LocationModel(
    longitude = longitude(),
    latitude = latitude(),
)

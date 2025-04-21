package esi.roadside.assistance.provider.main.domain.models

import com.mapbox.geojson.Point
import esi.roadside.assistance.provider.core.data.mappers.toLocation

data class LocationModel(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun fromString(location: String) = location.toLocation()
        fun fromPoint(point: Point) = LocationModel(
            latitude = point.latitude(),
            longitude = point.longitude()
        )
    }

    override fun toString() = "${latitude},${longitude}"
}

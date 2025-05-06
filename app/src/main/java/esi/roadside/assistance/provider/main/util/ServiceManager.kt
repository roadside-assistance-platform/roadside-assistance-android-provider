package esi.roadside.assistance.provider.main.util

import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.core.data.mappers.toLocation
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.main.data.dto.JsonDirectionsResponse
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationServiceModel
import esi.roadside.assistance.provider.main.domain.use_cases.DirectionsUseCase
import esi.roadside.assistance.provider.main.domain.use_cases.ReverseGeocoding

class ServiceManager(
    private val directionsUseCase: DirectionsUseCase,
    private val reverseGeocodingUseCase: ReverseGeocoding,
) {
    suspend fun processNewService(
        service: PolymorphicNotification.Service,
        location: LocationModel?,
        maxDistanceFilter: Boolean,
        maxDistance: Int
    ): NotificationServiceModel? {
        var directions = JsonDirectionsResponse("", emptyList(), "", emptyList())
        location?.let {
            directionsUseCase(it to service.serviceLocation.toLocation()).onSuccess {
                directions = it
            }
        }
        val distance = directions.routes.minOfOrNull { it.distance } ?: -1.0
        if (!maxDistanceFilter || (distance / 1000) <= maxDistance) {
            var locationString = ""
            reverseGeocodingUseCase(service.serviceLocation.toLocation()).onSuccess {
                locationString = it
            }
            return service.toNotificationServiceModel(
                directions = directions,
                locationString = locationString
            )
        }
        return null
    }
}
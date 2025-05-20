package esi.roadside.assistance.provider.main.domain.repository

import esi.roadside.assistance.provider.main.domain.PolymorphicNotification
import esi.roadside.assistance.provider.main.domain.models.LocationModel

sealed interface ServiceAction {
    data class NewService(
        val service: PolymorphicNotification.Service,
        val location: LocationModel?
    ): ServiceAction
    data class SelectService(val index: Int): ServiceAction
    data object UnSelectService: ServiceAction
    data class ServiceDone(
        val price: Int,
        val rating: Double?,
    ): ServiceAction
    data class ServiceRemoved(val serviceId: String, val exceptionId: String?): ServiceAction
    data class Accept(val location: LocationModel?): ServiceAction
    data class LocationUpdate(val location: LocationModel): ServiceAction
    data object Arrived: ServiceAction
    data object Finish: ServiceAction
}
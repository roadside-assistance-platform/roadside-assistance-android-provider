package esi.roadside.assistance.provider.main.domain.repository

import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.main.domain.models.AssistanceRequestModel
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.SubmitResponseModel

interface GeocodingRepo {
    suspend fun getLocationString(request: LocationModel): Result<String, DomainError>
    suspend fun getDistance(request: Pair<LocationModel, LocationModel>): Result<Double, DomainError>
}
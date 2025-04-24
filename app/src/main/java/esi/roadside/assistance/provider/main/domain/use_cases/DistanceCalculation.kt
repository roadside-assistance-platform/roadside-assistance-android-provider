package esi.roadside.assistance.provider.main.domain.use_cases

import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.repository.GeocodingRepo
import esi.roadside.assistance.provider.main.domain.repository.MainRepo

class DistanceCalculation(private val repo: GeocodingRepo) {
    suspend operator fun invoke(request: Pair<LocationModel, LocationModel>) = repo.getDistance(request)
}
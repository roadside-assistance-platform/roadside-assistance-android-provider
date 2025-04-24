package esi.roadside.assistance.provider.main.domain.use_cases

import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.repository.GeocodingRepo
import esi.roadside.assistance.provider.main.domain.repository.MainRepo

class ReverseGeocoding(private val repo: GeocodingRepo) {
    suspend operator fun invoke(request: LocationModel) = repo.getLocationString(request)
}
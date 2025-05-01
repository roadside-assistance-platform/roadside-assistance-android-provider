package esi.roadside.assistance.provider.main.domain.use_cases

import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.repository.GeocodingRepo
import esi.roadside.assistance.provider.main.domain.repository.MainRepo

class AcceptService(private val repo: MainRepo) {
    suspend operator fun invoke(request: Pair<String, String>) = repo.acceptService(request)
}
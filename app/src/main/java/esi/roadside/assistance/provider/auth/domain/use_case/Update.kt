package esi.roadside.assistance.provider.auth.domain.use_case

import esi.roadside.assistance.provider.auth.domain.models.UpdateModel
import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo

class Update(private val authRepo: AuthRepo) {
    suspend operator fun invoke(updateRequest: UpdateModel) = authRepo.update(updateRequest)
}
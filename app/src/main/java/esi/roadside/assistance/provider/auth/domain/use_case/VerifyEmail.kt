package esi.roadside.assistance.provider.auth.domain.use_case

import esi.roadside.assistance.provider.auth.domain.models.SendEmailModel
import esi.roadside.assistance.provider.auth.domain.models.VerifyEmailModel
import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo

class VerifyEmail(private val authRepo: AuthRepo) {
    suspend operator fun invoke(request: VerifyEmailModel) = authRepo.verifyEmail(request)
}
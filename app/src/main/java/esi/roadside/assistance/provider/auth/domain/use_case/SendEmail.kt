package esi.roadside.assistance.provider.auth.domain.use_case

import esi.roadside.assistance.provider.auth.domain.models.SendEmailModel
import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo

class SendEmail(private val authRepo: AuthRepo) {
    suspend operator fun invoke(request: SendEmailModel, forgot: Boolean = false) = authRepo.sendEmail(request, forgot)
}
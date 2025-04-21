package esi.roadside.assistance.provider.auth.domain.use_case

import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo

class ResetPassword(private val authRepo: AuthRepo) {
    suspend operator fun invoke(email: String) = authRepo.resetPassword(email)
}
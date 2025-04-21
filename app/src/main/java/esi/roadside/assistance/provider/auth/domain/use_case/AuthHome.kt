package esi.roadside.assistance.provider.auth.domain.use_case

import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo

class AuthHome(private val authRepo: AuthRepo) {
    suspend operator fun invoke() = authRepo.authHome()
}
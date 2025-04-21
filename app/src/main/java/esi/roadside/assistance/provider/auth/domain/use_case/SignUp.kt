package esi.roadside.assistance.provider.auth.domain.use_case

import esi.roadside.assistance.provider.auth.domain.models.SignupModel
import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo

class SignUp(private val authRepo: AuthRepo) {
    suspend operator fun invoke(signupRequest: SignupModel) = authRepo.signup(signupRequest)
}
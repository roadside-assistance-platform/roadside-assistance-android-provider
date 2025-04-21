package esi.roadside.assistance.provider.auth.domain.use_case

import esi.roadside.assistance.provider.auth.domain.models.LoginRequestModel
import esi.roadside.assistance.provider.auth.domain.models.AuthResponseModel
import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.domain.util.Result

class Login(private val authRepo: AuthRepo) {
    suspend operator fun invoke(request: LoginRequestModel): Result<AuthResponseModel, DomainError>  =
        authRepo.login(request)
}
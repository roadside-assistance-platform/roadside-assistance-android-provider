package esi.roadside.assistance.provider.auth.domain.repository

import esi.roadside.assistance.provider.auth.domain.models.LoginRequestModel
import esi.roadside.assistance.provider.auth.domain.models.AuthResponseModel
import esi.roadside.assistance.provider.auth.domain.models.SignupModel
import esi.roadside.assistance.provider.auth.domain.models.UpdateModel
import esi.roadside.assistance.provider.auth.domain.models.SendEmailModel
import esi.roadside.assistance.provider.auth.domain.models.VerifyEmailModel
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.domain.model.ProviderModel
import esi.roadside.assistance.provider.core.domain.util.Result

interface AuthRepo {
    suspend fun login(request: LoginRequestModel): Result<AuthResponseModel, DomainError>
    suspend fun signup(request: SignupModel): Result<AuthResponseModel, DomainError>
    suspend fun resetPassword(email: String, password: String): Result<Any, DomainError>
    suspend fun update(request: UpdateModel): Result<ProviderModel, DomainError>
    suspend fun sendEmail(request: SendEmailModel, forgot: Boolean = false): Result<Boolean, DomainError>
    suspend fun verifyEmail(request: VerifyEmailModel): Result<Boolean, DomainError>
    suspend fun authHome(): Result<Boolean, DomainError>
}
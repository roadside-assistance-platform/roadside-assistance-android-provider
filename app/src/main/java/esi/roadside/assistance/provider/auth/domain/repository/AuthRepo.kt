package esi.roadside.assistance.provider.auth.domain.repository

import androidx.credentials.GetCredentialResponse
import esi.roadside.assistance.provider.auth.domain.models.LoginRequestModel
import esi.roadside.assistance.provider.auth.domain.models.AuthResponseModel
import esi.roadside.assistance.provider.auth.domain.models.SignupModel
import esi.roadside.assistance.provider.auth.domain.models.UpdateModel
import esi.roadside.assistance.provider.auth.domain.models.SendEmailModel
import esi.roadside.assistance.provider.auth.domain.models.VerifyEmailModel
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.domain.model.ClientModel
import esi.roadside.assistance.provider.core.domain.util.Result

interface AuthRepo {
    suspend fun login(request: LoginRequestModel): Result<AuthResponseModel, DomainError>
    suspend fun signup(request: SignupModel): Result<AuthResponseModel, DomainError>
    suspend fun resetPassword(email: String): Result<ClientModel, DomainError>
    suspend fun update(request: UpdateModel): Result<ClientModel, DomainError>
    suspend fun sendEmail(request: SendEmailModel): Result<Boolean, DomainError>
    suspend fun verifyEmail(request: VerifyEmailModel): Result<Boolean, DomainError>
    suspend fun authHome(): Result<Boolean, DomainError>
}
package esi.roadside.assistance.provider.auth.data

import esi.roadside.assistance.provider.auth.data.dto.AuthResponse
import esi.roadside.assistance.provider.auth.domain.models.AuthResponseModel
import esi.roadside.assistance.provider.auth.domain.models.LoginRequestModel
import esi.roadside.assistance.provider.auth.domain.models.SendEmailModel
import esi.roadside.assistance.provider.auth.domain.models.SignupModel
import esi.roadside.assistance.provider.auth.domain.models.UpdateModel
import esi.roadside.assistance.provider.auth.domain.models.VerifyEmailModel
import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo
import esi.roadside.assistance.provider.core.data.Endpoints
import esi.roadside.assistance.provider.core.data.dto.Provider
import esi.roadside.assistance.provider.core.data.networking.CallType
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.data.networking.constructUrl
import esi.roadside.assistance.provider.core.data.networking.safeCall
import esi.roadside.assistance.provider.core.domain.model.ProviderModel
import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.core.domain.util.map
import esi.roadside.assistance.provider.main.data.dto.UpdateResponse
import esi.roadside.assistance.provider.main.domain.models.IsApprovedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class AuthRepoImpl(
    private val persistentCookieStorage: PersistentCookieStorage,
    private val client: HttpClient,
) : AuthRepo {
    override suspend fun login(request: LoginRequestModel): Result<AuthResponseModel, DomainError> {
        val remote = request.toLoginRequest()
        return safeCall<AuthResponse>(CallType.LOGIN) {
            client.post(constructUrl(Endpoints.LOGIN)) {
                setBody(remote)
            }.body()
        }.map { response ->
            response.toLoginResponseModel()
        }
    }

    override suspend fun signup(request: SignupModel): Result<AuthResponseModel, DomainError> {
        val remote = request.toSignupRequest()
        return safeCall<AuthResponse>(CallType.SIGNUP) {
            client.post(constructUrl(Endpoints.SIGNUP)) {
                setBody(remote)
            }.body()
        }.map { response ->
            response.toLoginResponseModel()
        }
    }

    override suspend fun resetPassword(email: String, password: String): Result<Any, DomainError> {
        return safeCall<Any>(CallType.RESET_PASSWORD) {
            client.post(constructUrl(Endpoints.RESET_PASSWORD)) {
                setBody(
                    mapOf(
                        "email" to email,
                        "newPassword" to password
                    )
                )
            }.body()
        }.map { true }
    }

    override suspend fun update(request: UpdateModel): Result<ProviderModel, DomainError> {
        persistentCookieStorage.logAllCookies()
        val remote = request.toUpdateRequest()
        return safeCall<UpdateResponse>(CallType.UPDATE) {
            client.put(constructUrl("${Endpoints.UPDATE_PROFILE}${request.id}")) {
                setBody(remote)
            }.body()
        }.map { response ->
            response.data.user.toProviderModel()
        }
    }

    override suspend fun sendEmail(request: SendEmailModel, forgot: Boolean): Result<Boolean, DomainError> {
        return safeCall<Any>(if (forgot) CallType.SEND_FORGOT_EMAIL else CallType.SEND_EMAIL) {
            client.post(constructUrl(
                if (forgot) Endpoints.SEND_FORGOT_EMAIL else Endpoints.SEND_EMAIL
            )) {
                setBody(request)
            }.body()
        }.map { true }
    }

    override suspend fun verifyEmail(request: VerifyEmailModel): Result<Boolean, DomainError> {
        return safeCall<Any>(CallType.VERIFY_EMAIL) {
            client.post(constructUrl(Endpoints.VERIFY_EMAIL)) {
                setBody(request)
            }.body()
        }.map { true }
    }

    override suspend fun authHome(): Result<Boolean, DomainError> {
        return safeCall<String>(CallType.HOME) {
            client.get(constructUrl(Endpoints.HOME))
        }.map { true }
    }

    override suspend fun getIsApproved(id: String): Result<Boolean, DomainError> {
        return safeCall<IsApprovedResponse> {
            client.get(constructUrl(Endpoints.IS_APPROVED).replace("{id}", id))
        }.map {
            it.data.isApproved
        }
    }
}

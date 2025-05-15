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

    override suspend fun resetPassword(email: String, password: String): Result<ProviderModel, DomainError> {
        TODO("Not yet implemented")
    }

    override suspend fun update(request: UpdateModel): Result<ProviderModel, DomainError> {
        persistentCookieStorage.logAllCookies()
        val remote = request.toUpdateRequest()
        return safeCall<Provider>(CallType.UPDATE) {
            client.put(constructUrl("${Endpoints.UPDATE_PROFILE}${request.id}")) {
                setBody(remote)
            }.body()
        }.map { response ->
            response.toProviderModel()
        }
    }

    override suspend fun sendEmail(request: SendEmailModel): Result<Boolean, DomainError> {
        return safeCall<Any>(CallType.SEND_EMAIL) {
            client.post(constructUrl(Endpoints.SEND_EMAIL)) {
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
}

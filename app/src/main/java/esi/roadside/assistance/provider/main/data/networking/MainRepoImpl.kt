package esi.roadside.assistance.provider.main.data.networking

import esi.roadside.assistance.provider.auth.data.PersistentCookieStorage
import esi.roadside.assistance.provider.core.data.Endpoints
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.data.networking.constructUrl
import esi.roadside.assistance.provider.core.data.networking.safeCall
import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.main.domain.models.AssistanceRequestModel
import esi.roadside.assistance.provider.main.domain.models.SubmitResponseModel
import esi.roadside.assistance.provider.main.domain.repository.MainRepo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class MainRepoImpl(
    private val client: HttpClient,
    private val storage: PersistentCookieStorage,
): MainRepo {
    override suspend fun submitRequest(request: AssistanceRequestModel): Result<SubmitResponseModel, DomainError> =
        safeCall<SubmitResponseModel> {
            client.post(constructUrl(Endpoints.SERVICE_CREATE)) {
                setBody(request.toAssistanceRequest())
            }.body()
        }

    override suspend fun logout() = storage.deleteCookie()
}
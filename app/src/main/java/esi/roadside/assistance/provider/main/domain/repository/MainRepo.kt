package esi.roadside.assistance.provider.main.domain.repository

import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.main.domain.models.AssistanceRequestModel
import esi.roadside.assistance.provider.main.domain.models.SubmitResponseModel

interface MainRepo {
    suspend fun submitRequest(request: AssistanceRequestModel): Result<SubmitResponseModel, DomainError>
    suspend fun logout()
}
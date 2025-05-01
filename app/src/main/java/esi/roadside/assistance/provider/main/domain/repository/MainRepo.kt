package esi.roadside.assistance.provider.main.domain.repository

import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.main.domain.models.ClientInfoModel
import esi.roadside.assistance.provider.main.domain.models.ServiceModel

interface MainRepo {
    suspend fun getClientInfo(request: String): Result<ClientInfoModel, DomainError>
    suspend fun acceptService(request: Pair<String, String>): Result<ServiceModel, DomainError>
    suspend fun logout()
}
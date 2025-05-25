package esi.roadside.assistance.provider.main.data.networking

import esi.roadside.assistance.provider.auth.data.PersistentCookieStorage
import esi.roadside.assistance.provider.core.data.Endpoints
import esi.roadside.assistance.provider.core.data.mappers.toLocation
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.data.networking.constructUrl
import esi.roadside.assistance.provider.core.data.networking.safeCall
import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.core.domain.util.map
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.main.data.dto.FetchServicesDto
import esi.roadside.assistance.provider.main.domain.models.ClientInfo
import esi.roadside.assistance.provider.main.domain.models.ClientInfoModel
import esi.roadside.assistance.provider.main.domain.models.FetchServicesDataModel
import esi.roadside.assistance.provider.main.domain.models.FetchServicesModel
import esi.roadside.assistance.provider.main.domain.models.ServiceModel
import esi.roadside.assistance.provider.main.domain.models.ServiceResponse
import esi.roadside.assistance.provider.main.domain.repository.GeocodingRepo
import esi.roadside.assistance.provider.main.domain.repository.MainRepo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class MainRepoImpl(
    private val geocodingRepo: GeocodingRepo,
    private val client: HttpClient,
    private val storage: PersistentCookieStorage,
): MainRepo {
    override suspend fun getClientInfo(request: String): Result<ClientInfoModel, DomainError> =
        safeCall<ClientInfo> {
            client.get(constructUrl("${Endpoints.GET_CLIENT_INFO}$request")).body()
        }.map {
            it.toClientInfoModel()
        }

    override suspend fun acceptService(request: Pair<String, String>): Result<ServiceModel, DomainError> =
        safeCall<ServiceResponse> {
            client.put(constructUrl("${Endpoints.SERVICE_UPDATE}${request.first}")) {
                val json = """
                    {
                        "providerId": "${request.second}"
                    }
                """.trimIndent()
                setBody(json)
            }.body()
        }.map {
            var service = it.data.service.toServiceModel("")
            geocodingRepo.getLocationString(service.serviceLocation).onSuccess { locationString ->
                service = service.copy(serviceLocationString = locationString)
            }
            service
        }

    override suspend fun fetchServices(id: String): Result<FetchServicesModel, DomainError> {
        return safeCall<FetchServicesDto> {
            client.get(constructUrl("${Endpoints.SERVICES}$id")).body()
        }.map {
            FetchServicesModel(
                status = it.status,
                data = it.data.toDomainModel {
//                    var location = ""
//                    geocodingRepo.getLocationString(it.serviceLocation.toLocation()).onSuccess { locationString ->
//                        location = locationString
//                    }
//                    location
                    ""
                }.also {
                    it.copy(services = it.services.sortedByDescending { service -> service.createdAt })
                }
            )
        }
    }

    override suspend fun logout() = storage.deleteCookie()
}
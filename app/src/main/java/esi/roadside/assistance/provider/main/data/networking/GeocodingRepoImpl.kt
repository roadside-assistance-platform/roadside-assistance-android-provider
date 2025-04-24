package esi.roadside.assistance.provider.main.data.networking

import android.content.Context
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.data.BaseUrls
import esi.roadside.assistance.provider.core.data.Endpoints
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.data.networking.constructUrl
import esi.roadside.assistance.provider.core.data.networking.safeCall
import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.core.domain.util.map
import esi.roadside.assistance.provider.main.data.dto.LocationResponse
import esi.roadside.assistance.provider.main.data.dto.Route
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.repository.GeocodingRepo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.parameters

class GeocodingRepoImpl(
    private val context: Context,
    private val client: HttpClient,
): GeocodingRepo {
    override suspend fun getLocationString(request: LocationModel): Result<String, DomainError> {
        return safeCall<List<LocationResponse>> {
            client.get(constructUrl(Endpoints.GET_LOCATION_STRING, BaseUrls.MAPBOX_GEOCODING)) {
                parameters {
                    append("access_token", context.getString(R.string.mapbox_access_token))
                    append("longitude", "${request.longitude}")
                    append("latitude", "${request.latitude}")
                    append("types", "address")
                }
            }.body()
        }.map {
            it.first().q
        }
    }

    override suspend fun getDistance(request: Pair<LocationModel, LocationModel>): Result<Double, DomainError> {
        return safeCall<List<Route>> {
            client.get(
                constructUrl(
                    Endpoints.getRoutesEndpoint("driving", request),
                    BaseUrls.MAPBOX_GEOCODING
                )
            )  {
                parameters {
                    append("access_token", context.getString(R.string.mapbox_access_token))
                }
            }.body()
        }.map {
            it.minOf { it.distance }
        }
    }
}
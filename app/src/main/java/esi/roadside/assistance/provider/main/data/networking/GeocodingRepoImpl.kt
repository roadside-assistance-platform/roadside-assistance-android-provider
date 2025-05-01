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
import esi.roadside.assistance.provider.main.data.dto.DirectionsResponse
import esi.roadside.assistance.provider.main.data.dto.JsonDirectionsResponse
import esi.roadside.assistance.provider.main.data.dto.LocationResponse
import esi.roadside.assistance.provider.main.data.dto.Route
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.geocoding.ReverseGeocodingResponse
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
        return safeCall<ReverseGeocodingResponse> {
            client.get(constructUrl(Endpoints.getLocationStringEndpoint(request.longitude, request.latitude, context), BaseUrls.MAPBOX_GEOCODING)).body()
        }.map {
            it.features.firstOrNull {
                it.properties.full_address.isNotEmpty()
            }?.properties?.full_address ?: ""
        }
    }

        override suspend fun getDistance(request: Pair<LocationModel, LocationModel>): Result<JsonDirectionsResponse, DomainError> {
        return safeCall<JsonDirectionsResponse> {
            client.get(
                constructUrl(
                    Endpoints.getRoutesEndpoint("driving", request, context),
                    BaseUrls.MAPBOX_DRIVING
                )
            ).body()
        }
    }
}
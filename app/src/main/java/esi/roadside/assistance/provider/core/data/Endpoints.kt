package esi.roadside.assistance.provider.core.data

import android.content.Context
import esi.roadside.assistance.provider.BuildConfig
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.main.domain.models.LocationModel

object Endpoints {
    const val LOGIN = "/provider/login"
    const val SIGNUP = "/provider/signup"
    const val RESET_PASSWORD = "/provider/reset-password"
    const val UPDATE_PROFILE = "/provider/update/"
    const val HOME = "/home"
    const val IS_APPROVED = "/provider/is-approved/{id}"
    const val SEND_EMAIL = "/email/send-code"
    const val SEND_FORGOT_EMAIL = "/email/forgot-code"
    const val VERIFY_EMAIL = "/email/verify-code"
    const val GET_CLIENT_INFO = "/client/info/"
    const val SERVICE_UPDATE = "/service/update/"
    const val GET_LOCATION_STRING = "/reverse/geocoding?access_token={access_token}&longitude={longitude}&latitude={latitude}&types=address"
    const val GET_ROUTES = "/{profile}/{coordinates}?access_token={access_token}&geometries=geojson"

    fun getRoutesEndpoint(profile: String, coordinates: Pair<LocationModel, LocationModel>, context: Context) =
        GET_ROUTES.replace("{profile}", profile)
            .replace(
                "{coordinates}",
                "${coordinates.first};${coordinates.second}"
            )
            .replace(
                "{access_token}",
                context.getString(R.string.mapbox_access_token)
            )

    fun getLocationStringEndpoint(longitude: Double, latitude: Double, context: Context) =
        GET_LOCATION_STRING.replace("{access_token}", context.getString(R.string.mapbox_access_token))
            .replace("{longitude}", longitude.toString())
            .replace("{latitude}", latitude.toString())
}

object BaseUrls {
    const val API = BuildConfig.BASE_URL
    const val MAPBOX_GEOCODING = BuildConfig.MAPBOX_GEOCODING
    const val MAPBOX_DRIVING = BuildConfig.MAPBOX_DRIVING
}
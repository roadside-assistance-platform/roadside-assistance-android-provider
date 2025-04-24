package esi.roadside.assistance.provider.core.data

import esi.roadside.assistance.provider.BuildConfig
import esi.roadside.assistance.provider.main.domain.models.LocationModel

object Endpoints {
    const val LOGIN = "/provider/login"
    const val SIGNUP = "/provider/signup"
    const val RESET_PASSWORD = "/provider/reset-password"
    const val UPDATE_PROFILE = "/provider/update/"
    const val HOME = "/home"
    const val SEND_EMAIL = "/email/send-code"
    const val VERIFY_EMAIL = "/email/verify-code"
    const val GET_CLIENT_INFO = "/client/info"
    const val SERVICE_UPDATE = "/service/update/"
    const val SERVICE_DELETE = "/service/delete/"
    const val GET_LOCATION_STRING = "/location/get-string"
    const val GET_ROUTES = "/{profile}/{coordinates}"

    fun getRoutesEndpoint(profile: String, coordinates: Pair<LocationModel, LocationModel>) =
        GET_ROUTES.replace("{profile}", profile)
            .replace(
                "{coordinates}",
                "${coordinates.first};${coordinates.second}"
            )
}

object BaseUrls {
    const val API = BuildConfig.BASE_URL
    const val MAPBOX_GEOCODING = BuildConfig.MAPBOX_GEOCODING
}
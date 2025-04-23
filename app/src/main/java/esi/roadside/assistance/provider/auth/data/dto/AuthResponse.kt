package esi.roadside.assistance.provider.auth.data.dto

import esi.roadside.assistance.provider.auth.domain.models.AuthResponseModel
import esi.roadside.assistance.provider.core.data.dto.Provider
import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
    val user: Provider,
)

@Serializable
data class AuthResponse(
    val status: String,
    val message: String? = null,
    val data: ResponseData
) {
    fun toLoginResponseModel() = AuthResponseModel(
        message = message,
        user = data.user.toProviderModel()
    )
}

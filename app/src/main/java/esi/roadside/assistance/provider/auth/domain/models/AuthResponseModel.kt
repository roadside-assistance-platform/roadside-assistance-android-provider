package esi.roadside.assistance.provider.auth.domain.models

import esi.roadside.assistance.provider.core.domain.model.ProviderModel

data class AuthResponseModel(
    val message: String? = null,
    val user: ProviderModel
)

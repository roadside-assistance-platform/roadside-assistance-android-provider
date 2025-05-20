package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.core.data.dto.Service
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val service: Service
)

@Serializable
data class ServiceResponse(
    val status: String,
    val data: Data
)

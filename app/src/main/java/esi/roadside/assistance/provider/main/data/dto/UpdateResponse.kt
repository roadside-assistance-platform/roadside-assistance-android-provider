package esi.roadside.assistance.provider.main.data.dto

import esi.roadside.assistance.provider.core.data.dto.Provider
import kotlinx.serialization.Serializable

@Serializable
data class UpdateResponse(
    val status: String,
    val message: String,
    val data: UpdateData
)

@Serializable
data class UpdateData(
    val user: Provider
)
package esi.roadside.assistance.provider.main.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class IsApprovedResponse(
    val data: ApprovedData,
    val status: String,
)

@Serializable
data class ApprovedData(
    val isApproved: Boolean
)

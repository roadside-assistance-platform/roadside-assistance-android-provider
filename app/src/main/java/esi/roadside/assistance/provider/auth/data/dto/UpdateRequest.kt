package esi.roadside.assistance.provider.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequest(
    val fullName: String? = null,
    val phone: String? = null,
    val photo: String? = null
)
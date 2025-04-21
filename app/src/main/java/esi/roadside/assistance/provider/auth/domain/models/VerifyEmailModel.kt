package esi.roadside.assistance.provider.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailModel(
    val email: String,
    val code: String,
)

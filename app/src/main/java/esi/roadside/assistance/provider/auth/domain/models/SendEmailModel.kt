package esi.roadside.assistance.provider.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SendEmailModel(
    val email: String
)

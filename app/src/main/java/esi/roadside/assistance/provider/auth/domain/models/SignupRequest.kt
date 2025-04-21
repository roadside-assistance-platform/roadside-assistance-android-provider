package esi.roadside.assistance.provider.auth.domain.models

data class SignupRequest(
    val email: String,
    val password: String,
    val role: String = "client"
)

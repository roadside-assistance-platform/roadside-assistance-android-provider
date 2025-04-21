package esi.roadside.assistance.provider.auth.domain.models

import esi.roadside.assistance.provider.auth.data.dto.LoginRequest

data class LoginRequestModel(
    val email: String,
    val password: String,
) {
    fun toLoginRequest() = LoginRequest(
        email = email,
        password = password
    )
}
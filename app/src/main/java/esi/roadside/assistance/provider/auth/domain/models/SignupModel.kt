package esi.roadside.assistance.provider.auth.domain.models

import esi.roadside.assistance.provider.auth.data.dto.SignupRequest

data class SignupModel(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val photo: String,
) {
    fun toSignupRequest() = SignupRequest(
        email = this@SignupModel.email,
        password = password,
        fullName = fullName,
        phone = phone,
        photo = photo
    )
}

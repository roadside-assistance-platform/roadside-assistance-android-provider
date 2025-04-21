package esi.roadside.assistance.provider.auth.data.dto

import esi.roadside.assistance.provider.auth.domain.models.SignupModel
import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val photo: String,
) {
    fun toSignupModel() = SignupModel(
        // complete
        email =email,
        password = password,
        fullName = fullName,
        phone = phone,
        photo = photo
    )
}

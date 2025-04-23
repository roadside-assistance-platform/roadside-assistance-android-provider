package esi.roadside.assistance.provider.auth.domain.models

import esi.roadside.assistance.provider.auth.data.dto.UpdateRequest
import esi.roadside.assistance.provider.main.domain.Categories

data class UpdateModel(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val photo: String? = null,
    val categories: Set<Categories> = emptySet(),
) {
    fun toUpdateRequest() = UpdateRequest(
        fullName = fullName,
        phone = phone,
        photo = photo,
        categories = categories
    )
}

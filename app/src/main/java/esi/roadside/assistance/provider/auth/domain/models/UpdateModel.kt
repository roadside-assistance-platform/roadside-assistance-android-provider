package esi.roadside.assistance.provider.auth.domain.models

import esi.roadside.assistance.provider.auth.data.dto.UpdateRequest
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.LocationModel

data class UpdateModel(
    val id: String,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val photo: String? = null,
    val location: LocationModel? = null,
    val categories: Set<Categories>? = null,
) {
    fun toUpdateRequest() = UpdateRequest(
        fullName = fullName,
        phone = phone,
        photo = photo,
        serviceCategories = categories,
        location = location?.toString()
    )
}

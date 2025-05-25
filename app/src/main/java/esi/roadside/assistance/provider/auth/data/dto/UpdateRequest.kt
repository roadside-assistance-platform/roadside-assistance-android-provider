package esi.roadside.assistance.provider.auth.data.dto

import esi.roadside.assistance.provider.main.domain.Categories
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequest(
    val fullName: String? = null,
    val phone: String? = null,
    val photo: String? = null,
    val serviceCategories: Set<Categories>? = null,
)
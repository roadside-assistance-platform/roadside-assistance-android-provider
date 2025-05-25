package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.main.domain.Categories
import kotlinx.serialization.Serializable

@Serializable
data class ProviderInfo(
    val id: String = "",
    val fullName: String = "",
    val phone: String = "",
    val photo: String = "",
    val email: String = "",
    val categories: Set<Categories> = emptySet(),
    val averageRating: Double? = null,
)
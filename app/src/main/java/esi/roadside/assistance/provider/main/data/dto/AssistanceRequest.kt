package esi.roadside.assistance.provider.main.data.dto

import esi.roadside.assistance.provider.main.domain.Categories
import kotlinx.serialization.Serializable

@Serializable
class AssistanceRequest(
    val description: String = "",
    val serviceCategory: Categories,
    val serviceLocation: String,
    val providerId: String = "",
    val price: Int,
)
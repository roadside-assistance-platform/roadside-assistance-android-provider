package esi.roadside.assistance.provider.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Admin(
    val iso_3166_1: String,
    val iso_3166_1_alpha3: String
)
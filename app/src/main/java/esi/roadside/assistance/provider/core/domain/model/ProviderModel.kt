package esi.roadside.assistance.provider.core.domain.model

import esi.roadside.assistance.provider.core.data.dto.Provider
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.ProviderInfo
import esi.roadside.assistance.provider.main.domain.models.ServiceModel
import esi.roadside.assistance.provider.main.presentation.models.ProviderUi
import java.time.ZonedDateTime
import kotlin.collections.Set

data class ProviderModel(
    val id: String = "",
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String,
    val photo: String,
    val categories: Set<Categories>,
    val services: List<ServiceModel> = emptyList(),
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime
) {
    fun toProvider() = Provider(
        id = id,
        fullName = fullName,
        email = email,
        password = password,
        phone = phone,
        photo = photo,
        serviceCategories = categories,
        services = services.map { it.toService() },
        createdAt = createdAt.toOffsetDateTime().toString(),
        updatedAt = updatedAt.toOffsetDateTime().toString()
    )
    fun toProviderUi() = ProviderUi(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        categories = categories,
    )
    fun toProviderInfo() = ProviderInfo(
        id = id,
        fullName = fullName,
        phone = phone,
        photo = photo,
        email = email,
        categories = categories
    )
}
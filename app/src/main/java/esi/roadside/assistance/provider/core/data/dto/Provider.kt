package esi.roadside.assistance.provider.core.data.dto

import esi.roadside.assistance.provider.core.domain.model.ProviderModel
import esi.roadside.assistance.provider.main.domain.Categories
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class Provider(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val photo: String? = "",
    val serviceCategories: Set<Categories> = emptySet(),
    val services: List<Service> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = "",
) {
    fun toProviderModel() = ProviderModel(
        id = id,
        fullName = fullName,
        email = email,
        password = password,
        phone = phone,
        photo = photo ?: "",
        services = services.map { it.toServiceModel("") },
        createdAt = try {
            OffsetDateTime.parse(createdAt)
        } catch(_: Exception) {
            OffsetDateTime.now()
        }.toLocalDateTime().atZone(ZoneId.systemDefault()),
        categories = serviceCategories,
        updatedAt = try {
            OffsetDateTime.parse(updatedAt)
        } catch(_: Exception) {
            OffsetDateTime.now()
        }.toLocalDateTime().atZone(ZoneId.systemDefault())
    )
}

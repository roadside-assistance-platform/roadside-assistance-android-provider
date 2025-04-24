package esi.roadside.assistance.provider.main.data.dto

import esi.roadside.assistance.provider.core.data.dto.Service
import esi.roadside.assistance.provider.main.domain.models.ClientModel
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class Client(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val photo: String? = "",
    val services: List<Service> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = "",
) {
    fun toClientModel() = ClientModel(
        id = id,
        fullName = fullName,
        email = email,
        password = password,
        phone = phone,
        photo = photo ?: "",
        createdAt = try {
            OffsetDateTime.parse(createdAt)
        } catch(_: Exception) {
            OffsetDateTime.now()
        }.toLocalDateTime().atZone(ZoneId.systemDefault()),
        updatedAt = try {
            OffsetDateTime.parse(updatedAt)
        } catch(_: Exception) {
            OffsetDateTime.now()
        }.toLocalDateTime().atZone(ZoneId.systemDefault())
    )
}

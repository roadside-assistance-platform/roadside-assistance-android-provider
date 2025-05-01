package esi.roadside.assistance.provider.main.domain.models

import esi.roadside.assistance.provider.main.data.dto.Client
import esi.roadside.assistance.provider.main.presentation.models.ClientUi
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class ClientInfo(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val photo: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
) {
    fun toClientInfoModel() = ClientInfoModel(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        photo = photo,
        createdAt = ZonedDateTime.parse(createdAt),
        updatedAt = ZonedDateTime.parse(updatedAt),
    )
}


data class ClientInfoModel(
    val id: String = "",
    val fullName: String,
    val email: String,
    val phone: String,
    val photo: String,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
)

data class ClientModel(
    val id: String = "",
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String,
    val photo: String,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime
) {
    fun toClient() = Client(
        id = id,
        fullName = fullName,
        email = email,
        password = password,
        phone = phone,
        photo = photo,
        createdAt = createdAt.toOffsetDateTime().toString(),
        updatedAt = updatedAt.toOffsetDateTime().toString()
    )
    fun toClientUi() = ClientUi(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
    )
}
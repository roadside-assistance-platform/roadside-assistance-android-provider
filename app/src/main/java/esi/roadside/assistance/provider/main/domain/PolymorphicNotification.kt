package esi.roadside.assistance.provider.main.domain

import esi.roadside.assistance.provider.core.data.dto.Comment
import esi.roadside.assistance.provider.main.data.dto.JsonDirectionsResponse
import esi.roadside.assistance.provider.main.domain.models.ClientInfo
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationServiceModel
import esi.roadside.assistance.provider.main.domain.models.ProviderInfo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import java.time.Instant
import java.time.ZoneId

@Serializable(with = PolymorphicNotification.Serializer::class)
sealed interface PolymorphicNotification {
    @Serializable
    data class Service(
        val id: String = "",
        val client: ClientInfo = ClientInfo(),
        val providerId: String? = null,
        val price: Int = 0,
        val description: String = "",
        val serviceRating: Float = 0f,
        val serviceLocation: String = "",
        val done: Boolean = false,
        val serviceCategory: Categories = Categories.OTHER,
        val createdAt: Long = 0,
        val updatedAt: Long = 0,
        val comments: List<Comment> = emptyList()
    ) : PolymorphicNotification {
        fun toNotificationServiceModel(locationString: String, directions: JsonDirectionsResponse)
        = NotificationServiceModel(
            id = id,
            client = client,
            providerId = providerId,
            price = price,
            serviceRating = serviceRating,
            serviceLocation = LocationModel.fromString(serviceLocation),
            serviceLocationString = locationString,
            done = done,
            directions = directions,
            category = serviceCategory,
            createdAt = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()),
            updatedAt = Instant.ofEpochMilli(updatedAt).atZone(ZoneId.systemDefault()),
            comments = comments.map { it.toCommentModel() },
            description = description
        )
    }

    @Serializable
    data class UserNotification(
        val id: String,
        val title: String,
        val text: String,
        val isWarning: Boolean,
        val image: String?,
        val createdAt: String
    ) : PolymorphicNotification

    @Serializable
    data class LocationUpdate(
        val longitude: Double,
        val latitude: Double,
    ) : PolymorphicNotification

    @Serializable
    data class ServiceAcceptance(
        val provider: ProviderInfo,
    ) : PolymorphicNotification

    @Serializable
    data object ProviderArrived : PolymorphicNotification

    @Serializable
    data class ServiceDone(
        val done: Boolean,
        val price: Int
    ) : PolymorphicNotification


    object Serializer : KSerializer<PolymorphicNotification> {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            allowStructuredMapKeys = true
            prettyPrint = true
            coerceInputValues = true
            classDiscriminator = "polymorphicType"
            serializersModule = SerializersModule {
                polymorphic(PolymorphicNotification::class, Service::class, Service.serializer())
                polymorphic(PolymorphicNotification::class, UserNotification::class, UserNotification.serializer())
                polymorphic(PolymorphicNotification::class, LocationUpdate::class, LocationUpdate.serializer())
                polymorphic(PolymorphicNotification::class, ServiceAcceptance::class, ServiceAcceptance.serializer())
                polymorphic(PolymorphicNotification::class, ServiceDone::class, ServiceDone.serializer())
                polymorphic(PolymorphicNotification::class, ProviderArrived::class, ProviderArrived.serializer())
            }
        }

        override val descriptor: SerialDescriptor
            get() = PolymorphicSerializer(PolymorphicNotification::class).descriptor
        override fun serialize(encoder: Encoder, value: PolymorphicNotification) {
            val polymorphicSerializer = PolymorphicSerializer(PolymorphicNotification::class)
            val jsonString = json.encodeToString(polymorphicSerializer, value)
            val element = json.parseToJsonElement(jsonString)
            (encoder as JsonEncoder).encodeJsonElement(element)
        }

        override fun deserialize(decoder: Decoder): PolymorphicNotification {
            val jsonElement = (decoder as JsonDecoder).decodeJsonElement()
            println("${jsonElement.jsonObject["polymorphicType"]?.jsonPrimitive?.content}")
            return when (val itemType = jsonElement.jsonObject["polymorphicType"]?.jsonPrimitive?.content) {
                Service::class.qualifiedName -> json.decodeFromJsonElement(Service.serializer(), jsonElement)
                UserNotification::class.qualifiedName -> json.decodeFromJsonElement(UserNotification.serializer(), jsonElement)
                LocationUpdate::class.qualifiedName -> json.decodeFromJsonElement(LocationUpdate.serializer(), jsonElement)
                ServiceAcceptance::class.qualifiedName -> json.decodeFromJsonElement(ServiceAcceptance.serializer(), jsonElement)
                ServiceDone::class.qualifiedName -> json.decodeFromJsonElement(ServiceDone.serializer(), jsonElement)
                ProviderArrived::class.qualifiedName -> json.decodeFromJsonElement(ProviderArrived.serializer(), jsonElement)
                else -> throw SerializationException("Unknown itemType: $itemType")
            }
        }
    }
}
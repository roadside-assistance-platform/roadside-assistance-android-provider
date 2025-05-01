package esi.roadside.assistance.provider.main.util

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import esi.roadside.assistance.provider.BuildConfig
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification
import esi.roadside.assistance.provider.main.domain.models.ClientInfo
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class QueuesManager() {
    private val _notifications = Channel<PolymorphicNotification>()
    private val _userNotifications = Channel<PolymorphicNotification.UserNotification>()
    private val _services = Channel<PolymorphicNotification.Service>()
    val services = _services
    var channel: com.rabbitmq.client.Channel? = null

    init {
        connect()
    }

    fun finalize() {
        close()
    }

    private fun connect() : com.rabbitmq.client.Channel {
        if (channel == null)
            ConnectionFactory().apply {
                setUri(BuildConfig.CLOUDAMPQ_URL)
                useSslProtocol()
            }.newConnection().createChannel()
        return channel!!
    }

    fun close() {
        channel?.close()
        channel = null
    }

    private fun consume(queueName: String, exchanges: List<String> = emptyList()) {
        val channel = connect()
        val queue = channel.queueDeclare(queueName, true, false, false, null).queue
        val consumer = DeliverCallback { _, delivery ->
            String(delivery.body, Charsets.UTF_8).let { message ->
                try {
                    println("Received message: $message")
                    val deserialized = Json.decodeFromString<PolymorphicNotification>(message)
                    println("Deserialized message: $deserialized")
                    _notifications.trySend(deserialized)
                    when(deserialized) {
                        is PolymorphicNotification.Service -> _services.trySend(deserialized)
                        is PolymorphicNotification.UserNotification -> _userNotifications.trySend(deserialized)
                        is PolymorphicNotification.ServiceDone -> TODO()
                        else -> return@let
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            channel.basicAck(delivery.envelope.deliveryTag, false)
        }
        val cancelCallback = CancelCallback { consumerTag ->
            println("Consumer cancelled: $consumerTag")
        }
        if (exchanges.isNotEmpty())
            exchanges.forEach { exchange ->
                channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT)
                channel.queueBind(queue, exchange, "")
                channel.basicConsume(queue, false, consumer, cancelCallback)
            }
        else
            channel.basicConsume(queue, false, consumer, cancelCallback)
    }

    private fun publishToExchanges(exchanges: List<String>, message: PolymorphicNotification) {
        val channel = connect()
        exchanges.forEach { exchange ->
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT)
            channel.basicPublish(
                exchange,
                "",
                null,
                Json.encodeToString(PolymorphicNotification.serializer(), message).toByteArray()
            )
        }
    }

    private fun publishToQueue(queueName: String, message: PolymorphicNotification) {
        val channel = connect()
        channel.queueDeclare(queueName, true, false, false, null)
        channel.basicPublish(
            "",
            queueName,
            null,
            Json.encodeToString(PolymorphicNotification.serializer(), message).toByteArray()
        )
    }

    fun consumeUserNotifications(userId: String, type: String) = consume("notifications-$type-$userId")

    fun consumeCategoryQueues(categories: Set<Categories>) =
        consume("", categories.map {
            "${it.name.lowercase()}-notifications-exchange"
        })

    fun publishCategoryQueues(categories: Set<Categories>, message: PolymorphicNotification) =
        publishToExchanges(categories.map {
            "${it.name.lowercase()}-notifications-exchange"
        }, message)

    fun publishUserNotification(userId: String, type: String, message: PolymorphicNotification) =
        publishToQueue("notifications-$type-$userId", message)
}

fun main() = runBlocking {
    val queuesManager = QueuesManager()
    val categories = setOf(Categories.TOWING)
    val userNotification = PolymorphicNotification.UserNotification(
            id = "123",
            title = "New notification",
            text = "You have a new notification",
            isWarning = false,
            image = null,
            createdAt = "2023-10-01T12:00:00Z"
        )
    queuesManager.consumeCategoryQueues(categories)
    queuesManager.publishUserNotification(userNotification.id, "provider", userNotification)
    delay(1000L)
    repeat(10) {
        val category = Categories.entries[it % Categories.entries.size]
        val message = PolymorphicNotification.Service(
            id = it.toString(),
            description = "Towing service",
            serviceCategory = category,
            serviceLocation = "${LocationModel(-0.653163, 35.214580)}",
            providerId = "456",
            price = 100,
            client = ClientInfo(
                "id",
                "User $it",
                "email@example.com",
                "0123456789",
            ),
            serviceRating = 5f,
            done = false,
            comments = emptyList()
        )
        queuesManager.publishCategoryQueues(setOf(category), message)
        delay(2000L)
    }
}
package esi.roadside.assistance.provider.main.util

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.CancelCallback
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
    val notifications = _notifications
    var channel: com.rabbitmq.client.Channel? = null

    private fun connect() : com.rabbitmq.client.Channel {
        if (channel == null)
            channel = ConnectionFactory().apply {
                setUri(BuildConfig.CLOUDAMPQ_URL)
                useSslProtocol()
            }.newConnection().createChannel()
        return channel!!
    }

    fun close() {
        try {
            if (channel == null) return
            if (!channel!!.isOpen) return
            channel!!.close()
        } catch (_: Exception) {
            // Ignore exception
        } finally {
            channel = null
        }
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
    val client = ClientInfo(
        "1234567",
        "User",
        "email@example.com",
        "0123456789",
    )
    val userNotification = PolymorphicNotification.UserNotification(
            id = "123",
            title = "New notification",
            text = "You have a new notification",
            isWarning = false,
            image = null,
            createdAt = "2023-10-01T12:00:00Z"
        )
    queuesManager.consumeCategoryQueues(categories)
    queuesManager.consumeUserNotifications(client.id, "client")
    queuesManager.publishUserNotification(userNotification.id, "provider", userNotification)
    delay(1000L)
    repeat(10) {
        val category = Categories.entries[it % Categories.entries.size]
        val message = PolymorphicNotification.Service(
            id = it.toString(),
            description = "Towing service",
            serviceCategory = category,
            serviceLocation = "${LocationModel(-0.653163, 35.214580)}",
            provider = null,
            price = 100,
            client = client,
            serviceRating = 5f,
            done = false,
            comments = emptyList()
        )
        queuesManager.publishCategoryQueues(setOf(category), message)
        delay(2000L)
    }
}
package esi.roadside.assistance.provider.main.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import esi.roadside.assistance.provider.BuildConfig
import esi.roadside.assistance.provider.core.data.dto.Service
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.TypedNotification
import esi.roadside.assistance.provider.main.domain.models.UserNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

object NotificationListener {
    private val _userNotifications = Channel<UserNotification>()
    val userNotifications = _userNotifications
    private val _services = Channel<Service>()
    val services = _services

    fun listenForNotifications(providerId: String, categories: Set<Categories>, mapper: ObjectMapper) {
        val factory = ConnectionFactory()
        factory.setUri(BuildConfig.CLOUDAMPQ_URL)

        CoroutineScope(Dispatchers.IO).launch {
            val connection = factory.newConnection()
            val channel = connection.createChannel()
            val queueName = "provider-${providerId}-queue"
            channel.queueDeclare(queueName, true, false, false, null)
            categories.forEach { category ->
                val exchangeName = "${category}-notifications-exchange"
                println("Waiting for notifications... - $queueName - $category")
                channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT)
                channel.queueBind(queueName, exchangeName, "")
                val consumer = DeliverCallback { _, delivery ->
                    String(delivery.body, Charsets.UTF_8).let { message ->
                        println("Received message: $message")
                        try {
                            val deserialized =
                                mapper.readValue(message, TypedNotification::class.java)
                            val service = deserialized.data as Service
                            _services.trySend(service)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
//                        when(deserialized.type) {
//                            NotificationType.SERVICE -> {
//                            }
//                            NotificationType.USER_NOTIFICATION -> {
//                                val notification = deserialized.data as UserNotification
//                                _userNotifications.trySend(notification)
//                            }
//                        }
                    }
                    channel.basicAck(delivery.envelope.deliveryTag, false)
                }
                val cancelCallback = CancelCallback { consumerTag ->
                    println("Consumer cancelled: $consumerTag")
                }
                channel.basicConsume(queueName, true, consumer, cancelCallback)
            }
        }
    }
}
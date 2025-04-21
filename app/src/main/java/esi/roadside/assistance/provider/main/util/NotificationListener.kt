package esi.roadside.assistance.provider.main.util

import com.rabbitmq.client.*
import esi.roadside.assistance.provider.BuildConfig
import esi.roadside.assistance.provider.main.domain.models.NotificationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

object NotificationListener {
    private val _notifications = Channel<NotificationModel>()
    private var id = 0
    val notifications = _notifications.receiveAsFlow()

    fun listenForNotifications(userId: String) {
        val factory = ConnectionFactory()
        factory.setUri(BuildConfig.CLOUDAMPQ_URL)

        CoroutineScope(Dispatchers.IO).launch {
            val connection = factory.newConnection()
            val channel = connection.createChannel()

            val queueName = "notifications_$userId"
            channel.queueDeclare(queueName, false, false, false, null)

            println("Waiting for notifications...")

            val consumer = object : DefaultConsumer(channel) {
                override fun handleDelivery(
                    consumerTag: String?,
                    envelope: Envelope?,
                    properties: AMQP.BasicProperties?,
                    body: ByteArray?
                ) {
                    body?.let { String(it) }?.let { message ->
                        _notifications.trySend(
                            NotificationModel(
                                id = "${id++}",
                                title = "Notification $id",
                                text = message,
                                isWarning = false,
                                image = null,
                                createdAt = LocalDateTime.now()
                            )
                        )
                    }
                }
            }

            channel.basicConsume(queueName, true, consumer)
        }
    }
}
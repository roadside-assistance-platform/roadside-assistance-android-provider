package esi.roadside.assistance.provider.main.domain.repository

import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.core.data.SettingsDataStore
import esi.roadside.assistance.provider.core.data.mappers.toLocation
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.Event.ShowMainActivityMessage
import esi.roadside.assistance.provider.core.presentation.util.EventBus.sendEvent
import esi.roadside.assistance.provider.core.util.account.AccountManager
import esi.roadside.assistance.provider.main.data.dto.JsonDirectionsResponse
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification.LocationUpdate
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification.Message
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification.ProviderArrived
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification.ServiceAcceptance
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification.ServiceDone
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification.ServiceRemove
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.use_cases.AcceptService
import esi.roadside.assistance.provider.main.domain.use_cases.DirectionsUseCase
import esi.roadside.assistance.provider.main.domain.use_cases.ReverseGeocoding
import esi.roadside.assistance.provider.main.presentation.routes.home.ProviderState
import esi.roadside.assistance.provider.main.util.QueuesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ServiceManager(
    accountManager: AccountManager,
    private val notificationService: NotificationService,
    val queuesManager: QueuesManager,
    val dataStore: SettingsDataStore,
    val directionsUseCase: DirectionsUseCase,
    val reverseGeocodingUseCase: ReverseGeocoding,
    val acceptServiceUseCase: AcceptService,
) {
    private val _service = MutableStateFlow<ServiceState>(ServiceState())
    val service = _service.asStateFlow()

    val user = accountManager.getUserFlow().map { it.toProviderModel() }

    private val maxDistanceFilter = dataStore.maxDistanceFilter
    private val maxDistance = dataStore.maxDistance

     fun listen(
         scope: CoroutineScope,
         id: String,
         categories: Set<Categories>,
         onLocationRequest: suspend () -> LocationModel?
     ) {
        Log.i("ServiceManager", "listening: $id, $categories")
        if (categories.isNotEmpty()) queuesManager.consumeCategoryQueues(categories)
        scope.launch(Dispatchers.IO) {
            queuesManager.consumeUserNotifications(id, "provider")
        }
        scope.launch(Dispatchers.IO) {
            queuesManager.notifications.receiveAsFlow().collectLatest { notification ->
                Log.i("ServiceManager", "New message: $notification")
                when(notification) {
                    is PolymorphicNotification.Service -> {
                        onAction(ServiceAction.NewService(notification, onLocationRequest()))
                    }
                    is ServiceDone -> {
                        onAction(
                            ServiceAction.ServiceDone(
                                notification.price,
                                notification.rating
                            )
                        )
                    }
                    is ServiceRemove -> {
                        onAction(
                            ServiceAction.ServiceRemoved(
                                notification.serviceId,
                                notification.exception
                            )
                        )
                    }
                    is Message -> {

                    }
                    else -> return@collectLatest
                }
            }
        }
    }



    suspend fun onAction(action: ServiceAction) {
        when(action) {
            is ServiceAction.NewService -> {
                if (user.first().isApproved) {
                    var directions = JsonDirectionsResponse("", emptyList(), "", emptyList())
                    val serviceLocation = action.service.serviceLocation.toLocation()
                    action.location?.let {
                        directionsUseCase(it to serviceLocation).onSuccess {
                            directions = it
                        }
                    }
                    val distance = directions.routes.minOfOrNull { it.distance } ?: -1.0
                    if (!maxDistanceFilter.first() || (distance / 1000) <= maxDistance.first()) {
                        val content = StringBuilder()
                        var locationString = ""
                        val serviceInfo = action.service.toServiceInfo(
                            directions = directions,
                            locationString = locationString
                        )
                        content.append("Category: ${serviceInfo.category}\n")
                        if (serviceInfo.description.isNotBlank())
                            content.append("Description: ${serviceInfo.description}\n")
                        content.append("Distance: ${String.format(Locale.getDefault(), "%.2f", distance / 1000)} km\n")
                        reverseGeocodingUseCase(serviceLocation).onSuccess {
                            locationString = it
                            content.append("Location: $it\n")
                        }
                        notificationService.showNotification(
                            _service.value.services.size,
                            R.string.new_service_request,
                            content.toString(),
                            mapOf(
                                "from_notification" to true,
                                "service_id" to serviceInfo.id,
                            ),
                            NotificationCompat.Action(
                                R.drawable.baseline_check_24,
                                "Accept",
                                notificationService.getPendingIntent {
                                    val intent = it
                                    intent.putExtra("serviceId", serviceInfo.id)
                                    intent.putExtra("action", "accept")
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    intent
                                }
                            )
                        )
                        _service.update {
                            it.copy(
                                services = it.services + serviceInfo,
                                providerState = ProviderState.IDLE,
                                price = 0,
                                rating = 0.0
                            )
                        }
                    }
                }
            }

            is ServiceAction.ServiceDone -> {
                _service.value.serviceModel?.let { service ->
                    if (_service.value.providerState == ProviderState.WORKING) {
                        _service.update {
                            it.copy(
                                services = it.services.filter { it.id != service.id }
                            )
                        }
                        _service.update {
                            it.copy(
                                providerState = ProviderState.COMPLETED,
                                price = action.price,
                                rating = action.rating,
                            )
                        }
                    }
                }
                Log.i("ServiceManager", "Sending event")
                sendEvent(Event.RemoveRoutes)
                Log.i("ServiceManager", "Event sent")
            }
            is ServiceAction.ServiceRemoved -> {
                if (action.exceptionId?.let { it != user.first().id } != false) {
                    if (
                        _service.value.services.indexOfFirst { it.id == action.serviceId }
                        == _service.value.selected
                    )
                        _service.update {
                            it.copy(
                                providerState = ProviderState.IDLE,
                                price = 0,
                                rating = 0.0
                            )
                        }
                    _service.update {
                        it.copy(services = it.services.filter { it.id != action.serviceId })
                    }
                }
            }
            is ServiceAction.Accept -> {
                _service.value.selected?.let { index ->
                    val selectedService = _service.value.services[index]
                    acceptServiceUseCase(
                        selectedService.id to user.first().id
                    ).onSuccess {
                        sendEvent(ShowMainActivityMessage(R.string.service_accepted))
                    }
                    _service.update {
                        it.copy(
                            serviceModel = selectedService,
                            providerState = ProviderState.NAVIGATING,
                            clientInfo = selectedService.client
                        )
                    }
                    queuesManager.publishUserNotification(
                        selectedService.client.id,
                        "client",
                        ServiceAcceptance(
                            selectedService.id,
                            selectedService.category,
                            user.first().toProviderInfo()
                        )
                    )
                }
            }
            is ServiceAction.LocationUpdate -> {
                var eta: Double? = null
                _service.value.serviceModel?.serviceLocation?.let { serviceLocation ->
                    directionsUseCase(
                        action.location to serviceLocation
                    ).onSuccess { result ->
                        eta = result.routes.minOfOrNull { it.duration }
                    }
                }
                queuesManager.publishUserNotification(
                    _service.value.serviceModel!!.client.id,
                    "client",
                    LocationUpdate(action.location.longitude, action.location.latitude, eta)
                )
            }
            ServiceAction.Arrived -> {
                _service.update {
                    it.copy(providerState = ProviderState.WORKING)
                }
                _service.value.serviceModel?.let { service ->
                    queuesManager.publishUserNotification(
                        service.client.id,
                        "client",
                        ProviderArrived
                    )
                }
            }
            ServiceAction.Finish -> {
                queuesManager.publishCategoryQueues(
                    setOf(_service.value.serviceModel!!.category),
                    ServiceRemove(
                        serviceId = _service.value.serviceModel!!.id,
                        exception = user.first().id
                    )
                )
                _service.update {
                    it.copy(providerState = ProviderState.IDLE, selected = null)
                }
                sendEvent(Event.RemoveRoutes)
            }
            is ServiceAction.SelectService -> {
                _service.update {
                    it.copy(selected = action.index)
                }
            }
            ServiceAction.UnSelectService -> {
                _service.update {
                    it.copy(selected = null)
                }
            }
        }
    }

//    suspend fun processNewService(
//        service: PolymorphicNotification.Service,
//        location: LocationModel?,
//        maxDistanceFilter: Boolean,
//        maxDistance: Int
//    ): NotificationServiceModel? {
//        var directions = JsonDirectionsResponse("", emptyList(), "", emptyList())
//        location?.let {
//            directionsUseCase(it to service.serviceLocation.toLocation()).onSuccess {
//                directions = it
//            }
//        }
//        val distance = directions.routes.minOfOrNull { it.distance } ?: -1.0
//        if (!maxDistanceFilter || (distance / 1000) <= maxDistance) {
//            var locationString = ""
//            reverseGeocodingUseCase(service.serviceLocation.toLocation()).onSuccess {
//                locationString = it
//            }
//            return service.toNotificationServiceModel(
//                directions = directions,
//                locationString = locationString
//            )
//        }
//        return null
//    }
}
package esi.roadside.assistance.provider.main.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.domain.models.UpdateModel
import esi.roadside.assistance.provider.auth.domain.use_case.Cloudinary
import esi.roadside.assistance.provider.auth.domain.use_case.Update
import esi.roadside.assistance.provider.auth.util.dataStore
import esi.roadside.assistance.provider.core.data.SettingsDataStore
import esi.roadside.assistance.provider.core.data.dto.Provider
import esi.roadside.assistance.provider.core.data.dto.Service
import esi.roadside.assistance.provider.core.data.mappers.toLocation
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event.*
import esi.roadside.assistance.provider.core.presentation.util.Field
import esi.roadside.assistance.provider.core.presentation.util.ValidateInput
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import esi.roadside.assistance.provider.core.util.NotificationsReceiver
import esi.roadside.assistance.provider.main.data.dto.DirectionsResponse
import esi.roadside.assistance.provider.main.data.dto.JsonDirectionsResponse
import esi.roadside.assistance.provider.main.data.dto.RouteX
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification
import esi.roadside.assistance.provider.main.domain.PolymorphicNotification.*
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationServiceModel
import esi.roadside.assistance.provider.main.domain.models.UserNotificationModel
import esi.roadside.assistance.provider.main.domain.models.toLocationModel
import esi.roadside.assistance.provider.main.domain.use_cases.AcceptService
import esi.roadside.assistance.provider.main.domain.use_cases.DirectionsUseCase
import esi.roadside.assistance.provider.main.domain.use_cases.Logout
import esi.roadside.assistance.provider.main.domain.use_cases.ReverseGeocoding
import esi.roadside.assistance.provider.main.presentation.models.ProviderUi
import esi.roadside.assistance.provider.main.presentation.routes.home.HomeUiState
import esi.roadside.assistance.provider.main.presentation.routes.home.ProviderState
import esi.roadside.assistance.provider.main.presentation.routes.profile.ProfileUiState
import esi.roadside.assistance.provider.main.util.NotificationManager
import esi.roadside.assistance.provider.main.util.QueuesManager
import esi.roadside.assistance.provider.main.util.ServiceManager
import esi.roadside.assistance.provider.main.util.saveClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import kotlin.math.roundToInt

class MainViewModel(
    private val context: Context,
    val cloudinary: Cloudinary,
    val serviceManager: ServiceManager,
    val updateUseCase: Update,
    val acceptServiceUseCase: AcceptService,
    val logoutUseCase: Logout,
    val directionsUseCaseUseCase: DirectionsUseCase,
    val notificationManager: NotificationManager,
    val queuesManager: QueuesManager,
    val dataStore: SettingsDataStore,
): ViewModel() {
    private val _client = MutableStateFlow(ProviderUi())
    val client = _client.asStateFlow()

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    private val categories = _profileUiState.map { it.user.categories }
    val profileUiState = _profileUiState.asStateFlow()
    private val _provider = MutableStateFlow<Provider?>(null)

    private val _services = MutableStateFlow(emptyList<NotificationServiceModel>())

    private val _userNotification = MutableStateFlow(emptyList<UserNotificationModel>())
    val userNotification = _userNotification.asStateFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = combine(_homeUiState, _services) { state, services ->
        state.copy(services = services)
    }

    private val _currentService = MutableStateFlow<NotificationServiceModel?>(null)
    val currentService = _currentService.asStateFlow()

    private val _maxDistanceFilter = dataStore.maxDistanceFilter
    private val _maxDistance = dataStore.maxDistance

    init {
        viewModelScope.launch(Dispatchers.Main) {
            Log.i("MainViewModel", "init viewModel")
            launch(Dispatchers.IO) {
                context.dataStore.data.collectLatest { userPreferences ->
                    _provider.value = userPreferences.provider
                    _profileUiState.update {
                        it.copy(
                            user = userPreferences.provider.toProviderModel().toProviderUi(),
                            editUser = userPreferences.provider.toProviderModel().toProviderUi(),
                            photo = userPreferences.provider.photo ?: ""
                        )
                    }
                    launch(Dispatchers.IO) {
                        queuesManager.consumeUserNotifications(userPreferences.provider.id, "provider")
                    }
                }
            }
            launch(Dispatchers.IO) {
                categories.collectLatest { categories ->
                    Log.i("MainViewModel", "Categories: $categories")
                    withContext(Dispatchers.IO) {
                        if (categories.isNotEmpty()) {
                            queuesManager.consumeCategoryQueues(categories)
                        }
                    }
                }
            }
            launch(Dispatchers.IO) {
                queuesManager.notifications.consumeEach { notification ->
                    when(notification) {
                        is PolymorphicNotification.Service -> {
                            serviceManager.processNewService(
                                notification,
                                _homeUiState.value.location?.toLocationModel(),
                                _maxDistanceFilter.first(),
                                _maxDistance.first()
                            )?.let { service ->
                                _homeUiState.update {
                                    it.copy(
                                        providerState = ProviderState.IDLE,
                                        directions = null,
                                        selectedService = null,
                                        price = 0,
                                        rating = 0.0
                                    )
                                }
                                _currentService.value = service
                                _services.value = _services.value + service
                                notificationManager.showServiceNotification(
                                    service,
                                    _services.value.size
                                )
                                Log.i("MainViewModel", "New service: $service")
                            }
                        }
                        is ServiceDone -> {
                            _currentService.value?.let { service ->
                                if (_homeUiState.value.providerState == ProviderState.WORKING) {
                                    _services.value = _services.value.filter { it.id != service.id }
                                    _homeUiState.update {
                                        it.copy(
                                            providerState = ProviderState.COMPLETED,
                                            directions = null,
                                            selectedService = null,
                                            price = it.price,
                                            rating = it.rating,
                                        )
                                    }
                                }
                            }
                        }
                        is ServiceRemove -> {
                            _provider.value?.id?.let { id ->
                                if (notification.exception?.let { it != id } != false) {
                                    if (
                                        _services.value.indexOfFirst { it.id == notification.serviceId }
                                        == _homeUiState.value.selectedService
                                    )
                                        _homeUiState.update {
                                            it.copy(
                                                providerState = ProviderState.IDLE,
                                                directions = null,
                                                selectedService = null,
                                                price = 0,
                                                rating = 0.0
                                            )
                                        }
                                    _services.value = _services.value.filter {
                                        it.id != notification.serviceId
                                    }
                                }
                            }
                        }
                        else -> return@consumeEach
                    }
                }
            }
            launch(Dispatchers.IO) {
                _homeUiState.collectLatest {
                    if (it.providerState == ProviderState.NAVIGATING)
                        _currentService.value?.let { service ->
                            it.location?.let { location ->
                                directionsUseCaseUseCase(
                                    location.toLocationModel() to service.serviceLocation
                                ).onSuccess { directions ->
                                    directions.routes.minByOrNull { it.duration }?.let { route ->
                                        _homeUiState.update {
                                            it.copy(directions = route)
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    fun onAction(action: Action) {
        when(action) {
            Action.HideFinishDialog -> {
                _homeUiState.update {
                    it.copy(
                        providerState = ProviderState.IDLE
                    )
                }
            }
            is Action.SetLocation -> {
                _homeUiState.update {
                    it.copy(location = action.location)
                }
            }
            Action.ConfirmProfileEditing -> {
                val inputError = ValidateInput.validateUpdateProfile(
                    _profileUiState.value.editUser.fullName,
                    _profileUiState.value.editUser.email,
                    _profileUiState.value.editUser.phone
                )
                if (inputError != null)
                    _profileUiState.update {
                        it.copy(
                            fullNameError = inputError.takeIf { it.field == Field.FULL_NAME },
                            emailError = inputError.takeIf { it.field == Field.EMAIL },
                            phoneError = inputError.takeIf { it.field == Field.PHONE_NUMBER }
                        )
                    }
                else {
                    _profileUiState.update { it.copy(loading = true) }
                    viewModelScope.launch {
                        cloudinary(
                            _profileUiState.value.editUser.photo ?: "".toUri(),
                            onSuccess = { url ->
                                _profileUiState.update {
                                    it.copy(
                                        photo = url,
                                    )
                                }
                            },
                            onFailure = {
                                sendEvent(ShowMainActivityMessage(R.string.error))
                            },
                            onFinished = {
                                viewModelScope.launch {
                                    updateUseCase(_profileUiState.value.editUser.toUpdateModel().copy(
                                        photo = _profileUiState.value.photo
                                    ))
                                        .onSuccess {
                                            saveClient(context, it)
                                        }
                                        .onError {
                                            sendEvent(ShowMainActivityMessage(it.text))
                                        }
                                    _profileUiState.update {
                                        it.copy(
                                            enableEditing = false,
                                            fullNameError = null,
                                            emailError = null,
                                            phoneError = null
                                        )
                                    }
                                    _profileUiState.update { it.copy(loading = false) }
                                }
                            },
                            onProgress = {}
                        )
                    }
                }
            }
            Action.EnableProfileEditing -> {
                _profileUiState.update {
                    it.copy(enableEditing = true)
                }
            }
            Action.CancelProfileEditing -> {
                _profileUiState.update {
                    it.copy(enableEditing = false, editUser = it.user)
                }
            }
            is Action.Navigate -> sendEvent(MainNavigate(action.route))
            is Action.EditClient -> {
                _profileUiState.update {
                    it.copy(editUser = action.client)
                }
            }

            Action.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    sendEvent(ExitToAuthActivity)
                }
            }

            is Action.AcceptService -> {
                _homeUiState.update {
                    it.copy(loading = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    acceptServiceUseCase(
                        _services.value[action.index].id to _profileUiState.value.user.id
                    ).onSuccess {
                        sendEvent(ShowMainActivityMessage(R.string.service_accepted))
                    }
                    val service = _services.value[action.index]
                    _currentService.value = service
                    _provider.value?.let { provider ->
                        queuesManager.publishUserNotification(
                            service.client.id,
                            "client",
                            ServiceAcceptance(service.id, service.category, provider.toProviderInfo())
                        )
                        _homeUiState.value.location?.let { location ->
                            queuesManager.publishUserNotification(
                                service.client.id,
                                "client",
                                LocationUpdate(location.longitude(), location.latitude())
                            )
                        }
                    }
                    _homeUiState.update {
                        it.copy(providerState = ProviderState.NAVIGATING, loading = false)
                    }
                }
            }
            is Action.SelectService -> {
                _homeUiState.update { state ->
                    state.copy(
                        selectedService = action.index,
                        directions = _services.value
                            .getOrNull(action.index)
                            ?.directions
                            ?.routes
                            ?.minByOrNull { it.duration }
                    )
                }
            }
            Action.UnSelectService -> {
                _homeUiState.update {
                    it.copy(selectedService = null, directions = null)
                }
            }
            is Action.SendEvent -> {
                sendEvent(action.event)
            }
            Action.Arrived -> {
                _homeUiState.update {
                    it.copy(providerState = ProviderState.WORKING)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    _currentService.value?.let { service ->
                        queuesManager.publishUserNotification(
                            service.client.id,
                            "client",
                            ProviderArrived
                        )
                    }
                }
            }
        }
    }
}
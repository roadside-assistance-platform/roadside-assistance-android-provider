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
import esi.roadside.assistance.provider.main.presentation.routes.profile.ProfileUiState
import esi.roadside.assistance.provider.main.util.QueuesManager
import esi.roadside.assistance.provider.main.util.saveClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class MainViewModel(
    private val context: Context,
    val cloudinary: Cloudinary,
    val updateUseCase: Update,
    val acceptServiceUseCase: AcceptService,
    val logoutUseCase: Logout,
    val reverseGeocodingUseCase: ReverseGeocoding,
    val directionsUseCaseUseCase: DirectionsUseCase,
    val notificationService: NotificationService,
    val queuesManager: QueuesManager,
): ViewModel() {

    private val _client = MutableStateFlow(ProviderUi())
    val client = _client.asStateFlow()

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    private val categories = _profileUiState.map {
        it.user.categories
    }
    val profileUiState = _profileUiState.asStateFlow()

    private val _services = MutableStateFlow(emptyList<NotificationServiceModel>())

    private val _userNotification = MutableStateFlow(emptyList<UserNotificationModel>())
    val userNotification = _userNotification.asStateFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = combine(_homeUiState, _services) { state, services ->
        state.copy(services = services)
    }

    init {
        viewModelScope.launch(Dispatchers.Main) {
            Log.i("MainViewModel", "init viewModel")
            launch {
                context.dataStore.data.collectLatest { userPreferences ->
                    _profileUiState.update {
                        it.copy(
                            user = userPreferences.provider.toProviderModel().toProviderUi(),
                            editUser = userPreferences.provider.toProviderModel().toProviderUi(),
                            photo = userPreferences.provider.photo ?: ""
                        )
                    }
                }
            }
            launch {
                categories.collectLatest { categories ->
                    Log.i("MainViewModel", "Categories: $categories")
                    withContext(Dispatchers.IO) {
                        if (categories.isNotEmpty()) {
                            queuesManager.consumeCategoryQueues(categories)
                        }
                    }
                }
            }
            queuesManager.services.consumeEach { service ->
                viewModelScope.launch {
                    var directions = JsonDirectionsResponse("", emptyList(), "", emptyList())
                    Log.i("MainViewModel", "Location: ${_homeUiState.value.location}")
                    _homeUiState.value.location?.let { location ->
                        directionsUseCaseUseCase(
                            location.toLocationModel() to service.serviceLocation.toLocation()
                        ).onSuccess {
                            directions = it
                        }
                    }
                    val distance = directions.routes.minOfOrNull { it.distance } ?: -1.0
                    if (distance / 1000 <= 10) {
                        var locationString = ""
                        reverseGeocodingUseCase(service.serviceLocation.toLocation()).onSuccess {
                            locationString = it
                        }
                        val serviceModel = service.toNotificationServiceModel(
                            directions = directions,
                            locationString = locationString,
                        )
                        _services.value = _services.value + serviceModel
                        val sb = StringBuilder()
                        if (serviceModel.serviceLocationString.isNotEmpty())
                            sb.append("Location: ${serviceModel.serviceLocationString}.\n")
                        if (distance >= 0)
                            sb.append("Distance: ${(distance / 1000).roundToInt()} km.\n")
                        sb.append("Category: ${context.getString(serviceModel.category.text)}.\n")
                        notificationService.showNotification(
                            _services.value.size,
                            "New service request from ${serviceModel.client.fullName}.\n",
                            sb.toString(),
                            NotificationCompat.Action(
                                R.drawable.baseline_check_24,
                                context.getString(R.string.accept),
                                notificationService.getPendingIntent(
                                    Intent(
                                        context,
                                        NotificationsReceiver::class.java
                                    )
                                )
                            )
                        )
                    }
                }
            }
//            NotificationListener.userNotifications.consumeEach { notification ->
//                _userNotification.update {
//                    it + notification.toUserNotificationModel()
//                }
//            }
        }
    }

    fun onAction(action: Action) {
        when(action) {
            is Action.SetLocation -> {
                _homeUiState.update {
                    it.copy(location = action.location)
                }
                viewModelScope.launch {
                    action.location?.let { location ->
                        updateUseCase(
                            UpdateModel(
                                id = _client.value.id,
                                location = location.toLocationModel()
                            )
                        ).onSuccess {
                            _profileUiState.update { state ->
                                state.copy(
                                    user = it.toProviderUi(),
                                    editUser = it.toProviderUi()
                                )
                            }
                        }
                    }
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
                viewModelScope.launch {
                    acceptServiceUseCase(_services.value[action.index].id)
                        .onSuccess {
                            sendEvent(ShowMainActivityMessage(R.string.service_accepted))
                        }
                }
            }
            is Action.SelectService -> {
                _homeUiState.update {
                    it.copy(selectedService = action.index)
                }
            }
            Action.UnSelectService -> {
                _homeUiState.update {
                    it.copy(selectedService = null)
                }
            }
            is Action.SendEvent -> {
                sendEvent(action.event)
            }
        }
    }
}
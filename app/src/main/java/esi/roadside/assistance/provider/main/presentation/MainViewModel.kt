package esi.roadside.assistance.provider.main.presentation

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.ObjectMapper
import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.domain.models.UpdateModel
import esi.roadside.assistance.provider.auth.domain.use_case.Cloudinary
import esi.roadside.assistance.provider.auth.domain.use_case.Update
import esi.roadside.assistance.provider.auth.util.dataStore
import esi.roadside.assistance.provider.core.data.dto.Service
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event.*
import esi.roadside.assistance.provider.core.presentation.util.Field
import esi.roadside.assistance.provider.core.presentation.util.ValidateInput
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import esi.roadside.assistance.provider.main.domain.Categories
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.UserNotificationModel
import esi.roadside.assistance.provider.main.domain.models.toLocationModel
import esi.roadside.assistance.provider.main.domain.use_cases.AcceptService
import esi.roadside.assistance.provider.main.domain.use_cases.DistanceCalculation
import esi.roadside.assistance.provider.main.domain.use_cases.Logout
import esi.roadside.assistance.provider.main.domain.use_cases.ReverseGeocoding
import esi.roadside.assistance.provider.main.presentation.models.ProviderUi
import esi.roadside.assistance.provider.main.presentation.routes.home.HomeUiState
import esi.roadside.assistance.provider.main.presentation.routes.profile.ProfileUiState
import esi.roadside.assistance.provider.main.util.NotificationListener
import esi.roadside.assistance.provider.main.util.saveClient
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    val mapper: ObjectMapper,
    val cloudinary: Cloudinary,
    val updateUseCase: Update,
    val acceptServiceUseCase: AcceptService,
    val logoutUseCase: Logout,
    val reverseGeocodingUseCase: ReverseGeocoding,
    val distanceCalculationUseCase: DistanceCalculation,
    val notificationService: NotificationService
): ViewModel() {

    private val _client = MutableStateFlow(ProviderUi())
    val client = _client.asStateFlow()

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState = _profileUiState.asStateFlow()

    private val _services = MutableStateFlow(emptyList<Service>())

    private val _userNotification = MutableStateFlow(emptyList<UserNotificationModel>())
    val userNotification = _userNotification.asStateFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = combine(_homeUiState, _services) { state, services ->
        return@combine state.copy(
            services = services
                .map { service ->
                    var location = ""
                    reverseGeocodingUseCase(LocationModel.fromString(service.serviceLocation))
                        .onSuccess {
                            location = it
                        }
                    service.toServiceModel(location)
                }
        )
    }

    init {
        viewModelScope.launch {
            context.dataStore.data.collectLatest { userPreferences ->
                _profileUiState.update {
                    it.copy(
                        user = userPreferences.provider.toProviderModel().toProviderUi(),
                        editUser = userPreferences.provider.toProviderModel().toProviderUi(),
                        photo = userPreferences.provider.photo ?: ""
                    )
                }
            }
            NotificationListener.listenForNotifications(
                _profileUiState.value.user.id,
                _profileUiState.value.user.categories,
                mapper
            )
            NotificationListener.services.consumeEach { service ->
                viewModelScope.launch {
                    var distance = Double.POSITIVE_INFINITY
                    _homeUiState.value.location?.let { location ->
                        distanceCalculationUseCase(
                            LocationModel.fromString(service.serviceLocation)
                                    to location.toLocationModel()
                        ).onSuccess {
                            distance = it
                        }
                    }
                    if ((distance / 1000) <= 10) {
                        _services.update {
                            it + service
                        }
                        notificationService.showNotification(
                            "You have new request",
                            "It's ${distance / 1000} km away"
                        )
                    }
                }
            }
            NotificationListener.userNotifications.consumeEach { notification ->
                _userNotification.update {
                    it + notification.toUserNotificationModel()
                }
            }
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
        }
    }
}
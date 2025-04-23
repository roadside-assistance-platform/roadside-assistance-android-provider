package esi.roadside.assistance.provider.main.presentation

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.domain.use_case.Cloudinary
import esi.roadside.assistance.provider.auth.domain.use_case.Update
import esi.roadside.assistance.provider.auth.util.dataStore
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event.*
import esi.roadside.assistance.provider.core.presentation.util.Field
import esi.roadside.assistance.provider.core.presentation.util.ValidateInput
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import esi.roadside.assistance.provider.main.domain.models.LocationModel
import esi.roadside.assistance.provider.main.domain.models.NotificationModel
import esi.roadside.assistance.provider.main.domain.models.AssistanceRequestModel
import esi.roadside.assistance.provider.main.domain.use_cases.Logout
import esi.roadside.assistance.provider.main.domain.use_cases.SubmitRequest
import esi.roadside.assistance.provider.main.presentation.models.ProviderUi
import esi.roadside.assistance.provider.main.presentation.routes.home.HomeUiState
import esi.roadside.assistance.provider.main.presentation.routes.home.request.RequestAssistanceState
import esi.roadside.assistance.provider.main.presentation.routes.profile.ProfileUiState
import esi.roadside.assistance.provider.main.util.NotificationListener
import esi.roadside.assistance.provider.main.util.saveClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    val cloudinary: Cloudinary,
    val updateUseCase: Update,
    val submitRequestUseCase: SubmitRequest,
    val logoutUseCase: Logout,
): ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val _client = MutableStateFlow(ProviderUi())
    val client = _client.asStateFlow()

    private val _requestAssistanceState = MutableStateFlow(RequestAssistanceState())
    val requestAssistanceState = _requestAssistanceState.asStateFlow()

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState = _profileUiState.asStateFlow()

    private val _notifications = MutableStateFlow(listOf<NotificationModel>())
    val notifications = _notifications.asStateFlow()

    init {
        NotificationListener.listenForNotifications(_client.value.id)
        viewModelScope.launch {
            context.dataStore.data.collectLatest { userPreferences ->
                _profileUiState.update {
                    it.copy(
                        client = userPreferences.provider.toProviderModel().toProviderUi(),
                        editClient = userPreferences.provider.toProviderModel().toProviderUi(),
                        photo = userPreferences.provider.photo ?: ""
                    )
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
            }
            is Action.SelectCategory -> {
                _requestAssistanceState.update {
                    it.copy(category = action.category)
                }
            }
            is Action.SetDescription -> {
                _requestAssistanceState.update {
                    it.copy(description = action.description)
                }
            }
            Action.SubmitRequest -> {
                viewModelScope.launch {
                    _homeUiState.value.location?.let { location ->
                        submitRequestUseCase(
                            AssistanceRequestModel(
                                description = _requestAssistanceState.value.description,
                                serviceCategory = _requestAssistanceState.value.category,
                                serviceLocation = LocationModel.fromPoint(location),
                                price = 0
                            )
                        ).onSuccess {
                            sendEvent(ShowMainActivityMessage(R.string.request_submitted))
                        }.onError {
                            sendEvent(ShowMainActivityMessage(it.text))
                        }
                        _requestAssistanceState.update {
                            it.copy(sheetVisible = false)
                        }
                    }
                }
            }
            Action.ConfirmProfileEditing -> {
                val inputError = ValidateInput.validateUpdateProfile(
                    _profileUiState.value.editClient.fullName,
                    _profileUiState.value.editClient.email,
                    _profileUiState.value.editClient.phone
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
                            _profileUiState.value.editClient.photo ?: "".toUri(),
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
                                    updateUseCase(_profileUiState.value.editClient.toUpdateModel().copy(
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
                    it.copy(enableEditing = false, editClient = it.client)
                }
            }
            is Action.Navigate -> sendEvent(MainNavigate(action.route))
            is Action.EditClient -> {
                _profileUiState.update {
                    it.copy(editClient = action.client)
                }
            }
            Action.ShowRequestAssistance -> {
                _requestAssistanceState.update {
                    it.copy(sheetVisible = true)
                }
                sendEvent(ShowRequestAssistance)
            }
            Action.HideRequestAssistance -> {
                _requestAssistanceState.update {
                    it.copy(sheetVisible = false)
                }
                sendEvent(ShowRequestAssistance)
            }

            Action.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    sendEvent(ExitToAuthActivity)
                }
            }
        }
    }
}
package esi.roadside.assistance.provider.main.presentation.routes.profile

import esi.roadside.assistance.provider.R
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.auth.domain.use_case.Cloudinary
import esi.roadside.assistance.provider.auth.domain.use_case.Update
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.Event.*
import esi.roadside.assistance.provider.core.presentation.util.EventBus.sendEvent
import esi.roadside.assistance.provider.core.presentation.util.Field
import esi.roadside.assistance.provider.core.presentation.util.ValidateInput
import esi.roadside.assistance.provider.core.util.account.AccountManager
import esi.roadside.assistance.provider.main.domain.use_cases.Refresh
import esi.roadside.assistance.provider.main.util.QueuesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountManager: AccountManager,
    private val updateUseCase: Update,
    private val refreshUseCase: Refresh,
    private val cloudinary: Cloudinary,
    private val queuesManager: QueuesManager
): ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    private val _user = accountManager.getUserFlow()
    val state = combine(_state, _user) { state, account ->
        state.copy(
            user = state.user.copy(
                isApproved = account.isApproved
            )
        )
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            accountManager.getUserFlow().collectLatest { provider ->
                _state.update {
                    it.copy(
                        user = provider.toProviderModel().toProviderUi(),
                        editUser = provider.toProviderModel().toProviderUi(),
                        photo = provider.photo ?: ""
                    )
                }
                launch(Dispatchers.IO) {
                    queuesManager.consumeUserNotifications(provider.id, "provider")
                }
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.ConfirmProfileEditing -> {
                ValidateInput.validateUpdateProfile(
                    _state.value.editUser.fullName,
                    _state.value.editUser.email,
                    _state.value.editUser.phone
                )?.let { error ->
                    _state.update {
                        it.copy(
                            fullNameError = error.takeIf { it.field == Field.FULL_NAME },
                            emailError = error.takeIf { it.field == Field.EMAIL },
                            phoneError = error.takeIf { it.field == Field.PHONE_NUMBER }
                        )
                    }
                    return
                }
                _state.update { it.copy(loading = true) }
                viewModelScope.launch(Dispatchers.IO) {
                    cloudinary(
                        _state.value.editUser.photo ?: "".toUri(),
                        onSuccess = { url ->
                            _state.update {
                                it.copy(
                                    photo = url,
                                )
                            }
                        },
                        onFailure = {
                            viewModelScope.launch(Dispatchers.IO) {
                                sendEvent(ShowMainActivityMessage(R.string.error))
                            }
                        },
                        onFinished = {
                            viewModelScope.launch(Dispatchers.IO) {
                                updateUseCase(_state.value.editUser.toUpdateModel().copy(
                                    photo = _state.value.photo
                                )).onError {
                                    sendEvent(ShowMainActivityMessage(it.text))
                                }
                                _state.update {
                                    it.copy(
                                        enableEditing = false,
                                        fullNameError = null,
                                        emailError = null,
                                        phoneError = null
                                    )
                                }
                                _state.update { it.copy(loading = false) }
                            }
                        },
                        onProgress = {}
                    )
                }
            }
            ProfileAction.EnableProfileEditing -> {
                _state.update {
                    it.copy(enableEditing = true)
                }
            }
            ProfileAction.CancelProfileEditing -> {
                _state.update {
                    it.copy(enableEditing = false, editUser = it.user)
                }
            }
            is ProfileAction.EditUser -> {
                _state.update {
                    it.copy(editUser = action.user)
                }
            }
            ProfileAction.HideDialog -> {
                _state.update {
                    it.copy(dialog = false)
                }
            }
            ProfileAction.Refresh -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update {
                        it.copy(loading = true)
                    }
                    refreshUseCase()
                    _state.update {
                        it.copy(loading = false, dialog = !_user.first().isApproved)
                    }
                }
            }
            ProfileAction.ShowDialog -> {
                _state.update {
                    it.copy(dialog = true)
                }
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            accountManager.getUserFlow().collectLatest { user ->
                _state.update {
                    it.copy(
                        user = user.toProviderModel().toProviderUi(),
                        editUser = user.toProviderModel().toProviderUi(),
                        photo = user.photo ?: ""
                    )
                }
            }
        }
    }
}
package esi.roadside.assistance.provider.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.domain.use_case.AuthHome
import esi.roadside.assistance.provider.auth.presentation.Action.GoToForgotPassword
import esi.roadside.assistance.provider.auth.presentation.Action.GoToLogin
import esi.roadside.assistance.provider.auth.presentation.Action.GoToSignup
import esi.roadside.assistance.provider.auth.presentation.Action.HideAuthError
import esi.roadside.assistance.provider.auth.presentation.Action.Initiate
import esi.roadside.assistance.provider.auth.presentation.Action.NextStep
import esi.roadside.assistance.provider.auth.presentation.Action.PreviousStep
import esi.roadside.assistance.provider.auth.presentation.Action.ShowAuthError
import esi.roadside.assistance.provider.auth.presentation.Action.Skip
import esi.roadside.assistance.provider.auth.presentation.screens.AuthUiState
import esi.roadside.assistance.provider.auth.presentation.util.loggedIn
import esi.roadside.assistance.provider.auth.util.account.AccountManager
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event.AuthNavigate
import esi.roadside.assistance.provider.core.presentation.util.Event.AuthNavigateBackward
import esi.roadside.assistance.provider.core.presentation.util.Event.AuthShowError
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val accountManager: AccountManager,
    private val authHomeUseCase: AuthHome,
): ViewModel() {
    private val _step = MutableStateFlow(0)
    val step = _step.asStateFlow()

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun onAction(action: Action) {
        when(action) {
            Initiate -> {
                _state.update {
                    it.copy(loading = true)
                }
                viewModelScope.launch {
                    authHomeUseCase()
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    error = null,
                                    errorDialogVisible = false,
                                    loading = false
                                )
                            }
                            if (it) loggedIn(accountManager)
                        }.onError { error ->
                            _state.update {
                                it.copy(
                                    error = error,
                                    errorDialogVisible = error == DomainError.NO_INTERNET,
                                    loading = false,
                                    action = R.string.retry to {
                                        onAction(Initiate)
                                    }
                                )
                            }
                            println(error)
                        }
                }
            }
            is GoToLogin -> {
                sendEvent(AuthNavigate(NavRoutes.Login))
            }
            is GoToSignup -> {
                sendEvent(AuthNavigate(NavRoutes.Signup))
            }
            is GoToForgotPassword -> {
                sendEvent(AuthNavigate(NavRoutes.ForgotPassword))
            }
            is ShowAuthError -> {
                sendEvent(AuthShowError(action.error))
            }
            HideAuthError -> {
                _state.update {
                    it.copy(errorDialogVisible = false, error = null)
                }
            }
            NextStep -> {
                _step.value++
            }
            PreviousStep -> {
                _step.value--
            }
            Skip -> {
                _step.value = 2
            }
            Action.Back -> {
                sendEvent(AuthNavigateBackward)
            }
        }
    }
}
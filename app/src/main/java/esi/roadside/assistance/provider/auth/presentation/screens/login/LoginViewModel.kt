package esi.roadside.assistance.provider.auth.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.auth.domain.models.LoginRequestModel
import esi.roadside.assistance.provider.auth.domain.use_case.Login
import kotlinx.coroutines.flow.update
import esi.roadside.assistance.provider.auth.presentation.util.loggedIn
import esi.roadside.assistance.provider.auth.util.account.AccountManager
import esi.roadside.assistance.provider.auth.util.account.SignInResult
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.Field
import esi.roadside.assistance.provider.core.presentation.util.ValidateInput
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    val accountManager: AccountManager,
    val loginUseCase: Login
): ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        when(action) {
            is LoginAction.SetEmail -> {
                _state.update {
                    it.copy(email = action.email)
                }
            }
            is LoginAction.SetPassword -> {
                _state.update {
                    it.copy(password = action.password)
                }
            }
            LoginAction.Autofill -> {
                viewModelScope.launch {
                    val result = accountManager.signIn()
                    if (result is SignInResult.Success) {
                        _state.update {
                            it.copy(email = result.username, password = result.password)
                        }
                    }
                }
            }
            LoginAction.Login -> {
                ValidateInput.validateLogin(_state.value.email, _state.value.password)?.let { error ->
                    _state.update {
                        it.copy(
                            emailError = error.second.takeIf { error.first == Field.EMAIL },
                            passwordError = error.second.takeIf { error.first == Field.PASSWORD },
                        )
                    }
                    return
                }
                _state.update {
                    it.copy(loading = true)
                }
                viewModelScope.launch {
                    loginUseCase(
                        LoginRequestModel(
                            email = _state.value.email,
                            password = _state.value.password
                        )
                    ).onSuccess {
                        loggedIn(accountManager, it.user)
                    }.onError {
                        println(it)
                        sendEvent(Event.AuthShowError(it))
                        _state.update {
                            it.copy(loading = false)
                        }
                    }
                }
            }
            LoginAction.TogglePasswordHidden -> {
                _state.update {
                    it.copy(passwordHidden = !it.passwordHidden)
                }
            }
        }
    }
}
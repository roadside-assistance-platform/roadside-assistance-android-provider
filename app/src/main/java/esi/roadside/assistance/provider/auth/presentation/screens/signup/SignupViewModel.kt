package esi.roadside.assistance.provider.auth.presentation.screens.signup

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.R
import esi.roadside.assistance.provider.auth.domain.models.SendEmailModel
import esi.roadside.assistance.provider.auth.domain.models.SignupModel
import esi.roadside.assistance.provider.auth.domain.models.VerifyEmailModel
import esi.roadside.assistance.provider.auth.domain.use_case.Cloudinary
import esi.roadside.assistance.provider.auth.domain.use_case.SendEmail
import esi.roadside.assistance.provider.auth.domain.use_case.SignUp
import esi.roadside.assistance.provider.auth.domain.use_case.VerifyEmail
import esi.roadside.assistance.provider.auth.presentation.Action
import esi.roadside.assistance.provider.auth.presentation.NavRoutes
import esi.roadside.assistance.provider.auth.presentation.screens.signup.SignupAction.*
import esi.roadside.assistance.provider.auth.presentation.screens.signup.OtpState
import esi.roadside.assistance.provider.auth.presentation.OtpAction
import esi.roadside.assistance.provider.auth.presentation.util.loggedIn
import esi.roadside.assistance.provider.auth.util.account.AccountManager
import esi.roadside.assistance.provider.core.domain.util.onError
import esi.roadside.assistance.provider.core.domain.util.onSuccess
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.Event.AuthNavigate
import esi.roadside.assistance.provider.core.presentation.util.Event.ImageUploadError
import esi.roadside.assistance.provider.core.presentation.util.Event.ShowAuthActivityMessage
import esi.roadside.assistance.provider.core.presentation.util.Field
import esi.roadside.assistance.provider.core.presentation.util.ValidateInput
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.filterNotNull
import kotlin.plus


class SignupViewModel(
    private val accountManager: AccountManager,
    private val cloudinaryUseCase: Cloudinary,
    private val sendEmailUseCase: SendEmail,
    private val signUpUseCase: SignUp,
    private val verifyEmailUseCase: VerifyEmail,
): ViewModel() {
    private val _state = MutableStateFlow(SignupUiState())
    val state = _state.asStateFlow()

    private val _otpState = MutableStateFlow(OtpState())
    val otpState = _otpState.asStateFlow()

    fun onOtpAction(action: OtpAction) {
        _otpState.update {
            if ((action is OtpAction.OnEnterNumber) and (it.isCodeComplete()))
                onAction(Verify)
            it.onOtpAction(action)
        }
    }

    fun onAction(action: SignupAction) {
        when(action) {
            is Signup -> {
                Log.d("SignupViewModel", "Signup")
                ValidateInput.validateSignup(
                    _state.value.email,
                    _state.value.password,
                    _state.value.confirmPassword,
                    _state.value.fullName,
                    _state.value.phoneNumber
                )?.let { error ->
                    Log.d("SignupViewModel", "Signup error: $error")
                    _state.update {
                        it.copy(
                            emailError = error.takeIf { error.field == Field.EMAIL },
                            passwordError = error.takeIf { error.field == Field.PASSWORD },
                            confirmPasswordError = error.takeIf { error.field == Field.CONFIRM_PASSWORD },
                            fullNameError = error.takeIf { error.field == Field.FULL_NAME },
                            phoneNumberError = error.takeIf { error.field == Field.PHONE_NUMBER }
                        )
                    }
                    return
                }
                _state.update {
                    it.copy(loading = true)
                }
                viewModelScope.launch {
                    var url: String? = null
                    cloudinaryUseCase(
                        image = _state.value.image ?: "".toUri(),
                        onSuccess = {
                            url = it
                        },
                        onProgress = { progress ->
                            _state.update {
                                it.copy(uploadProgress = progress)
                            }
                        },
                        onFailure = {
                            sendEvent(ImageUploadError)
                        },
                        onFinished = {
                            _state.update {
                                it.copy(photo = url ?: "_", loading = false)
                            }
                            onAction(SendCode(_state.value.email))
                        }
                    )
                }
            }
            is SendCodeToEmail -> onAction(SendCode(_state.value.email))
            is SendCode -> {
                _state.update {
                    it.copy(loading = true)
                }
                viewModelScope.launch {
                    sendEmailUseCase(SendEmailModel(action.email))
                        .onSuccess {
                            _state.update {
                                it.copy(loading = false)
                            }
                            _otpState.value = OtpState()
                            sendEvent(ShowAuthActivityMessage(R.string.verification_email_sent))
                            sendEvent(AuthNavigate(NavRoutes.VerifyEmail))
                        }
                        .onError {
                            _state.update {
                                it.copy(loading = false)
                            }
                            sendEvent(Event.AuthShowError(it))
                        }
                }
            }
            is Verify -> {
                _state.update {
                    it.copy(loading = true)
                }
                viewModelScope.launch {
                    verifyEmailUseCase(
                        VerifyEmailModel(
                            _state.value.email,
                            _otpState.value.code.filterNotNull().joinToString(""),
                        )
                    ).onSuccess {
                        signUpUseCase(
                            SignupModel(
                                email = _state.value.email,
                                password = _state.value.password,
                                fullName = _state.value.fullName,
                                phone = _state.value.phoneNumber,
                                photo = _state.value.photo,
                                categories = _state.value.categories,
                            )
                        ).onSuccess { response ->
                            accountManager.signUp(
                                _state.value.email,
                                _state.value.password
                            )
                            _state.update { it.copy(loading = false) }
                            loggedIn(accountManager, response.user)
                        }.onError {
                            _state.update {
                                it.copy(loading = false)
                            }
                            sendEvent(Event.AuthShowError(it))
                        }
                    }.onError {
                        _state.update {
                            it.copy(loading = false)
                        }
                        sendEvent(Event.AuthShowError(it))
                    }
                }
            }

            is SetConfirmPassword -> {
                _state.update {
                    it.copy(confirmPassword = action.confirmPassword)
                }
            }
            is SetEmail -> {
                _state.update {
                    it.copy(email = action.email)
                }
            }
            is SetFullName -> {
                _state.update {
                    it.copy(fullName = action.fullName)
                }
            }
            is SetImage -> {
                _state.update {
                    it.copy(image = action.image)
                }
            }
            is SetPassword -> {
                _state.update {
                    it.copy(password = action.password)
                }
            }

            is SetPhoneNumber -> {
                _state.update {
                    it.copy(phoneNumber = action.phoneNumber)
                }
            }
            is SetVerifyEmailCode -> {
                _state.update {
                    it.copy(verifyEmailCode = action.code)
                }
            }
            ToggleConfirmPasswordHidden -> {
                _state.update {
                    it.copy(confirmPasswordHidden = !it.confirmPasswordHidden)
                }
            }
            TogglePasswordHidden -> {
                _state.update {
                    it.copy(passwordHidden = !it.passwordHidden)
                }
            }
            GoToSignup2 -> {
                ValidateInput.validateSignup1(
                    _state.value.fullName,
                    _state.value.phoneNumber
                )?.let { error ->
                    _state.update {
                        it.copy(
                            fullNameError = error.takeIf { error.field == Field.FULL_NAME },
                            phoneNumberError = error.takeIf { error.field == Field.PHONE_NUMBER },
                        )
                    }
                    return
                }
                if (_state.value.categories.isEmpty()) {
                    sendEvent(ShowAuthActivityMessage(R.string.select_at_least_category))
                    return
                }
                sendEvent(AuthNavigate(NavRoutes.Signup2))
            }
            is AddCategory -> {
                _state.update {
                    it.copy(categories = it.categories.plus(setOf(action.category)))
                }
            }
            is RemoveCategory -> {
                _state.update {
                    it.copy(categories = it.categories.minus(action.category))
                }
            }
        }
    }
}
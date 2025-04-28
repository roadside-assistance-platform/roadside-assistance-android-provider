package esi.roadside.assistance.provider.auth.presentation.screens.reset_password

import esi.roadside.assistance.provider.auth.presentation.screens.login.InputError
import esi.roadside.assistance.provider.auth.presentation.screens.signup.OtpState

data class ResetPasswordUiState(
    val email: String = "",
    val emailError: InputError? = null,
    val password: String = "",
    val passwordError: InputError? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: InputError? = null,
    val passwordHidden: Boolean = false,
    val confirmPasswordHidden: Boolean = false,
    val otpState: OtpState = OtpState(),
    val loading: Boolean = false,
    val sent: Boolean = false,
)

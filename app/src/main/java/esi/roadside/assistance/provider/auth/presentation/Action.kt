package esi.roadside.assistance.provider.auth.presentation

import android.net.Uri
import androidx.credentials.GetCredentialResponse
import esi.roadside.assistance.provider.core.data.networking.DomainError
import esi.roadside.assistance.provider.main.domain.Categories

sealed interface Action {
    data object Initiate: Action

    data object NextStep: Action
    data object PreviousStep: Action
    data object Skip: Action

    data class SetLoginEmail(val email: String): Action
    data class SetLoginPassword(val password: String): Action
    data object ToggleLoginPasswordHidden: Action
    data object ToggleSignupPasswordHidden: Action
    data object ToggleSignupConfirmPasswordHidden: Action

    data class SetSignupFullName(val fullName: String): Action
    data class SetSignupPhoneNumber(val phoneNumber: String): Action
    data class SetSignupEmail(val email: String): Action
    data class SetSignupPassword(val password: String): Action
    data class SetSignupConfirmPassword(val confirmPassword: String): Action
    data class SetSignupImage(val image: Uri?): Action
    data class SetVerifyEmailCode(val code: String): Action

    data object Back: Action
    data object GoToLogin: Action
    data object GoToSignup: Action
    data object GoToSignup2: Action
    data object GoToForgotPassword: Action

    data object Login: Action
    data object Signup: Action
    data object Verify: Action
    data object SendCodeToEmail: Action
    data object SendCodeToResetEmail: Action
    data class SendCode(val email: String): Action
    data object Send: Action

    data class AddCategory(val category: Categories): Action
    data class RemoveCategory(val category: Categories): Action

    data class SetResetPasswordEmail(val email: String): Action
    data class SetResetPassword(val password: String): Action
    data class SetResetConfirmPassword(val password: String): Action
    data class SetResetPasswordHidden(val hidden: Boolean): Action
    data class SetResetConfirmPasswordHidden(val hidden: Boolean): Action

    data object SkipVerification: Action

    data class ShowAuthError(val error: DomainError): Action
    data object HideAuthError: Action
}
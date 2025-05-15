package esi.roadside.assistance.provider.auth.presentation.screens.signup

import android.net.Uri
import esi.roadside.assistance.provider.auth.presentation.Action
import esi.roadside.assistance.provider.main.domain.Categories

sealed interface SignupAction {
    data object GoToSignup2: SignupAction
    data object Signup: SignupAction

    data object SendCodeToEmail: SignupAction
    data class SendCode(val email: String): SignupAction
    data object Verify: SignupAction

    data object TogglePasswordHidden: SignupAction
    data object ToggleConfirmPasswordHidden: SignupAction

    data class SetFullName(val fullName: String): SignupAction
    data class SetPhoneNumber(val phoneNumber: String): SignupAction
    data class SetEmail(val email: String): SignupAction
    data class SetPassword(val password: String): SignupAction
    data class SetConfirmPassword(val confirmPassword: String): SignupAction
    data class SetImage(val image: Uri?): SignupAction
    data class SetVerifyEmailCode(val code: String): SignupAction

    data class AddCategory(val category: Categories): SignupAction
    data class RemoveCategory(val category: Categories): SignupAction
}
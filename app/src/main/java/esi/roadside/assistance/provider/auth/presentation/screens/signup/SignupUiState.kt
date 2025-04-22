package esi.roadside.assistance.provider.auth.presentation.screens.signup

import android.net.Uri
import esi.roadside.assistance.provider.auth.presentation.screens.login.InputError
import esi.roadside.assistance.provider.core.domain.Category
import esi.roadside.assistance.provider.main.domain.Categories

data class SignupUiState(
    val categories: Set<Categories> = emptySet(),
    val fullName: String = "",
    val fullNameError: InputError? = null,
    val phoneNumber: String = "",
    val phoneNumberError: InputError? = null,
    val email: String = "",
    val emailError: InputError? = null,
    val password: String = "",
    val passwordError: InputError? = null,
    val passwordHidden: Boolean = true,
    val confirmPassword: String = "",
    val confirmPasswordError: InputError? = null,
    val confirmPasswordHidden: Boolean = true,
    val image: Uri? = null,
    val photo: String = "",
    val verifyEmailCode: String = "",
    val verifyEmailCodeError: InputError? = null,
    val uploadProgress: Float = 0f,
    val loading: Boolean = false,
)

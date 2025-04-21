package esi.roadside.assistance.provider.auth.presentation.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: InputError? = null,
    val passwordError: InputError? = null,
    val passwordHidden: Boolean = true,
    val loading: Boolean = false,
)

package esi.roadside.assistance.provider.auth.presentation.screens.login

sealed interface LoginAction {
    data object Autofill: LoginAction
    data class SetEmail(val email: String): LoginAction
    data class SetPassword(val password: String): LoginAction
    data object TogglePasswordHidden: LoginAction
    data object Login: LoginAction
}
package esi.roadside.assistance.provider.auth.presentation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    data object Welcome : NavRoutes()

    @Serializable
    data object Login : NavRoutes()

    @Serializable
    data object Signup : NavRoutes()

    @Serializable
    data object Signup2 : NavRoutes()

    @Serializable
    data object VerifyEmail : NavRoutes()

    @Serializable
    data object ForgotPassword : NavRoutes()

    @Serializable
    data object VerifyResetPasswordEmail : NavRoutes()

    @Serializable
    data object ResetPassword : NavRoutes()
}
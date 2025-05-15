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

    data object Back: Action
    data object GoToLogin: Action
    data object GoToSignup: Action
    data object GoToForgotPassword: Action

    data class ShowAuthError(val error: DomainError): Action
    data object HideAuthError: Action
}
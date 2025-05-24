package esi.roadside.assistance.provider.main.presentation.routes.profile

import esi.roadside.assistance.provider.main.presentation.models.ProviderUi


sealed interface ProfileAction {
    data object EnableProfileEditing: ProfileAction
    data object CancelProfileEditing: ProfileAction
    data object ConfirmProfileEditing: ProfileAction
    data class EditUser(val user: ProviderUi): ProfileAction
    data object HideDialog: ProfileAction
    data object ShowDialog: ProfileAction
    data object Refresh: ProfileAction
}
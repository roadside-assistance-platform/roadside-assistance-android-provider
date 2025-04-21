package esi.roadside.assistance.provider.main.presentation.routes.profile

import esi.roadside.assistance.provider.auth.presentation.screens.login.InputError
import esi.roadside.assistance.provider.main.presentation.models.ClientUi

data class ProfileUiState(
    val client: ClientUi = ClientUi(),
    val editClient: ClientUi = ClientUi(),
    val photo: String = "",
    val fullNameError: InputError? = null,
    val emailError: InputError? = null,
    val phoneError: InputError? = null,
    val enableEditing: Boolean = false,
    val loading: Boolean = false,
)

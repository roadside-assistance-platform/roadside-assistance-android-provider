package esi.roadside.assistance.provider.main.presentation.routes.profile

import esi.roadside.assistance.provider.auth.presentation.screens.login.InputError
import esi.roadside.assistance.provider.core.data.dto.Provider
import esi.roadside.assistance.provider.main.presentation.models.ProviderUi

data class ProfileUiState(
    val user: ProviderUi = ProviderUi(),
    val editUser: ProviderUi = ProviderUi(),
    val photo: String = "",
    val fullNameError: InputError? = null,
    val emailError: InputError? = null,
    val phoneError: InputError? = null,
    val enableEditing: Boolean = false,
    val loading: Boolean = false,
    val dialog: Boolean = false,
)

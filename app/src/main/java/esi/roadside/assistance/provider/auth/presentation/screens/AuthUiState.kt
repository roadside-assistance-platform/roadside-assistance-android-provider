package esi.roadside.assistance.provider.auth.presentation.screens

import esi.roadside.assistance.provider.core.data.networking.DomainError

data class AuthUiState(
    val errorDialogVisible: Boolean = false,
    val error: DomainError? = null,
    val loading: Boolean = true,
    val action: Pair<Int, () -> Unit>? = null,
)
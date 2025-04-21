package esi.roadside.assistance.provider.auth.presentation.screens.signup

data class OtpState(
    val code: List<Int?> = (1..6).map { null },
    val focusedIndex: Int? = null,
)
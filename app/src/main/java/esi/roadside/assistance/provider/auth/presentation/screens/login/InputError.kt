package esi.roadside.assistance.provider.auth.presentation.screens.login

import esi.roadside.assistance.provider.core.presentation.util.Field

sealed class InputError(open val field: Field, open val text: Int) {
    data class Empty(override val field: Field, override val text: Int) : InputError(field, text)
    data class Invalid(override val field: Field, override val text: Int) : InputError(field, text)
    data class Short(override val field: Field, override val text: Int) : InputError(field, text)
    data class Weak(override val field: Field, override val text: Int) : InputError(field, text)
    data class Mismatch(override val field: Field, override val text: Int) : InputError(field, text)
    data class WrongPassword(override val field: Field, override val text: Int) : InputError(field, text)
}
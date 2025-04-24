package esi.roadside.assistance.provider.main.presentation.models

import android.net.Uri

data class ClientUi(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val location: String = "",
    val phone: String = "",
    val photo: Uri? = null
)
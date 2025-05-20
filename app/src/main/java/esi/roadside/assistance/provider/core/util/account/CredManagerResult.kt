package esi.roadside.assistance.provider.core.util.account

import androidx.credentials.PasswordCredential

data class CredManagerResult(val credentials: PasswordCredential? = null, val error: CredManagerError? = null)

data class CredManagerError(val errorMessage: String)
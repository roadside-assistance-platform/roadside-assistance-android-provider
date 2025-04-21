package esi.roadside.assistance.provider.auth.util.account

sealed interface SignInResult {
    data class Success(val username: String, val password: String): SignInResult
    data object Cancelled: SignInResult
    data object Failure: SignInResult
    data object NoCredentials: SignInResult
}
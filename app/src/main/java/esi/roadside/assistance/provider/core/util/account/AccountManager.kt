package esi.roadside.assistance.provider.core.util.account

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import esi.roadside.assistance.provider.auth.UserPreferences
import esi.roadside.assistance.provider.auth.util.dataStore
import esi.roadside.assistance.provider.core.data.dto.Provider
import esi.roadside.assistance.provider.core.domain.model.ProviderModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AccountManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signUp(username: String, password: String): SignUpResult {
        return try {
            credentialManager.createCredential(
                context = context,
                request = CreatePasswordRequest(
                    id = username,
                    password = password
                )
            )
            SignUpResult.Success(username)
        } catch (e: CreateCredentialCancellationException) {
            e.printStackTrace()
            SignUpResult.Cancelled
        } catch (e: CreateCredentialException) {
            e.printStackTrace()
            SignUpResult.Failure
        }
    }

    suspend fun signIn(): SignInResult {
        return try {
            val credentialResponse = credentialManager.getCredential(
                context = context,
                request = GetCredentialRequest(
                    credentialOptions = listOf(GetPasswordOption())
                )
            )
            val credential = credentialResponse.credential as? PasswordCredential
                ?: return SignInResult.Failure
            SignInResult.Success(credential.id, credential.password)
        } catch (e: GetCredentialCancellationException) {
            e.printStackTrace()
            SignInResult.Cancelled
        } catch (e: NoCredentialException) {
            e.printStackTrace()
            SignInResult.NoCredentials
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            SignInResult.Failure
        }
    }

    suspend fun getUser(): ProviderModel? {
        return context.dataStore.data.firstOrNull()?.provider?.toProviderModel()
    }

    suspend fun updateUser(provider: ProviderModel) {
        context.dataStore.updateData {
            UserPreferences(provider.toProvider())
        }
    }

    suspend fun updateIsApproved(isApproved: Boolean) {
        context.dataStore.updateData {
            it.copy(
                provider = it.provider.copy(isApproved = isApproved)
            )
        }
    }

    fun getUserFlow(): Flow<Provider> {
        return context.dataStore.data.map { it.provider }
    }
}
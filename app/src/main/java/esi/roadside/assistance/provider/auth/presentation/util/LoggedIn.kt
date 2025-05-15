package esi.roadside.assistance.provider.auth.presentation.util

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.auth.util.account.AccountManager
import esi.roadside.assistance.provider.core.domain.model.ProviderModel
import esi.roadside.assistance.provider.core.presentation.util.Event
import esi.roadside.assistance.provider.core.presentation.util.sendEvent
import kotlinx.coroutines.launch

fun ViewModel.loggedIn(
    accountManager: AccountManager,
    provider: ProviderModel? = null,
    launchMainActivity: Boolean = true
) {
    Log.i("Welcome", "Logged in successfully: $provider")
    provider?.let {
        viewModelScope.launch {
            accountManager.updateUser(provider)
        }
    }
    if (launchMainActivity) sendEvent(Event.LaunchMainActivity)
}
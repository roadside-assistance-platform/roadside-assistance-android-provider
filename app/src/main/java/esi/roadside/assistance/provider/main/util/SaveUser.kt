package esi.roadside.assistance.provider.main.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esi.roadside.assistance.provider.auth.UserPreferences
import esi.roadside.assistance.provider.auth.util.dataStore
import esi.roadside.assistance.provider.core.domain.model.ProviderModel
import kotlinx.coroutines.launch

fun ViewModel.saveClient(context: Context, client: ProviderModel) {
    viewModelScope.launch {
        context.dataStore.updateData {
            UserPreferences(client.toProvider())
        }
    }
}
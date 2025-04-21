package esi.roadside.assistance.provider.core.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import esi.roadside.assistance.provider.core.data.SettingsDataStore
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject


@Composable
fun isDark(): State<Boolean> {
    val datastore = koinInject<SettingsDataStore>()
    val isSystemDark = isSystemInDarkTheme()
    return datastore.theme.map {
        when (it) {
            "light" -> false
            "dark" -> true
            else -> isSystemDark
        }
    }.collectAsStateWithLifecycle(false)
}

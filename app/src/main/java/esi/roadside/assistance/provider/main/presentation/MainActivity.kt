package esi.roadside.assistance.provider.main.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import esi.roadside.assistance.provider.core.data.SettingsDataStore
import esi.roadside.assistance.provider.core.util.composables.SetSystemBarColors
import esi.roadside.assistance.provider.core.presentation.theme.AppTheme
import org.koin.android.ext.android.inject
import kotlin.getValue

class MainActivity : ComponentActivity() {
    val settingsDataStore by inject<SettingsDataStore>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetSystemBarColors(settingsDataStore)
            AppTheme {

            }
        }
    }
}
package esi.roadside.assistance.provider.core.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import esi.roadside.assistance.provider.core.util.dataFlow

class SettingsDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val THEME_KEY = stringPreferencesKey("app_theme")
        val MAX_DISTANCE_FILTER = booleanPreferencesKey("max_distance_filter")
        val MAX_DISTANCE = intPreferencesKey("max_distance")
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        val DYNAMIC_KEY = booleanPreferencesKey("dynamic_theme")
        val EXTRA_DARK_KEY = booleanPreferencesKey("extra_dark")
    }

    val theme = dataFlow(context.dataStore, THEME_KEY, "light")
    val dynamicColors = dataFlow(context.dataStore, DYNAMIC_KEY, false)
    val extraDark = dataFlow(context.dataStore, EXTRA_DARK_KEY, false)
    val maxDistanceFilter = dataFlow(context.dataStore, MAX_DISTANCE_FILTER, false)
    val maxDistance = dataFlow(context.dataStore, MAX_DISTANCE, 100)
    val language = dataFlow(context.dataStore, LANGUAGE_KEY, "system")

    suspend fun saveSettings(
        theme: String? = null,
        dynamic: Boolean? = null,
        extraDark: Boolean? = null,
        language: String? = null,
        maxDistanceFilter: Boolean? = null,
        maxDistance: Int? = null,
    ) {
        context.dataStore.edit { preferences ->
            theme?.let { preferences[THEME_KEY] = it }
            dynamic?.let { preferences[DYNAMIC_KEY] = it }
            extraDark?.let { preferences[EXTRA_DARK_KEY] = it }
            language?.let { preferences[LANGUAGE_KEY] = it }
            maxDistanceFilter?.let { preferences[MAX_DISTANCE_FILTER] = it }
            maxDistance?.let { preferences[MAX_DISTANCE] = it }
        }
    }
}

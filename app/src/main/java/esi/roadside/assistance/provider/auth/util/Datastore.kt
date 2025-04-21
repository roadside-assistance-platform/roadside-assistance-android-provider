package esi.roadside.assistance.provider.auth.util

import android.content.Context
import androidx.datastore.dataStore
import esi.roadside.assistance.provider.auth.UserPreferencesSerializer

val Context.dataStore by dataStore(
    fileName = "user-preferences",
    serializer = UserPreferencesSerializer
)
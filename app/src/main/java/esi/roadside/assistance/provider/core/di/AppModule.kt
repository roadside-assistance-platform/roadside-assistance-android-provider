package esi.roadside.assistance.provider.core.di

import esi.roadside.assistance.provider.core.data.SettingsDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        SettingsDataStore(androidContext())
    }
}
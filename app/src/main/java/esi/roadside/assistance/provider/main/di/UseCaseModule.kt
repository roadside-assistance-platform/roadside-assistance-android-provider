package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.main.domain.use_cases.AcceptService
import esi.roadside.assistance.provider.main.domain.use_cases.DirectionsUseCase
import esi.roadside.assistance.provider.main.domain.use_cases.FetchServices
import esi.roadside.assistance.provider.main.domain.use_cases.Logout
import esi.roadside.assistance.provider.main.domain.use_cases.Refresh
import esi.roadside.assistance.provider.main.domain.use_cases.ReverseGeocoding
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val useCaseModule = module {
    factory { ReverseGeocoding(get()) }
    factory { AcceptService(get()) }
    factory { DirectionsUseCase(get()) }
    factory { Logout(get()) }
    factory { Refresh(get(), get()) }
    factory { FetchServices(get(), get()) }
    factory { NotificationService(androidContext()) }
}
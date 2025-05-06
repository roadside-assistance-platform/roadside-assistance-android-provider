package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.main.data.networking.GeocodingRepoImpl
import esi.roadside.assistance.provider.main.data.networking.MainRepoImpl
import esi.roadside.assistance.provider.main.domain.repository.GeocodingRepo
import esi.roadside.assistance.provider.main.domain.repository.MainRepo
import esi.roadside.assistance.provider.main.util.NotificationManager
import esi.roadside.assistance.provider.main.util.QueuesManager
import esi.roadside.assistance.provider.main.util.ServiceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repoModule = module {
    single<MainRepo> { MainRepoImpl(get(), get(), get()) }
    single<GeocodingRepo> { GeocodingRepoImpl(androidContext(), get()) }
    single<QueuesManager> { QueuesManager() }
    single<ServiceManager> { ServiceManager(get(), get()) }
    single<NotificationManager> { NotificationManager(get(), androidContext()) }
}
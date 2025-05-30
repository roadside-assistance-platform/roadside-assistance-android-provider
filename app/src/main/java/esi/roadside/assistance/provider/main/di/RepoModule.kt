package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.NotificationService
import esi.roadside.assistance.provider.core.util.account.AccountManager
import esi.roadside.assistance.provider.main.data.networking.GeocodingRepoImpl
import esi.roadside.assistance.provider.main.data.networking.MainRepoImpl
import esi.roadside.assistance.provider.main.domain.repository.GeocodingRepo
import esi.roadside.assistance.provider.main.domain.repository.MainRepo
import esi.roadside.assistance.provider.main.domain.repository.ServiceManager
import esi.roadside.assistance.provider.main.util.QueuesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repoModule = module {
    single<MainRepo> { MainRepoImpl(get(), get(), get()) }
    single<GeocodingRepo> { GeocodingRepoImpl(androidContext(), get()) }
    single<QueuesManager> { QueuesManager() }
    single<NotificationService> { NotificationService(androidContext()) }
    single<AccountManager> { AccountManager(androidContext()) }
    single<ServiceManager> { ServiceManager(get(), get(), get(), get(), get(), get(), get()) }
}
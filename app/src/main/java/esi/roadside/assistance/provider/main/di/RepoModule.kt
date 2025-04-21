package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.main.data.networking.MainRepoImpl
import esi.roadside.assistance.provider.main.domain.repository.MainRepo
import org.koin.dsl.module

val repoModule = module {
    single<MainRepo> { MainRepoImpl(get(), get()) }
}
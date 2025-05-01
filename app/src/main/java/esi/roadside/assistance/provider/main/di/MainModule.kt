package esi.roadside.assistance.provider.main.di

import org.koin.dsl.module

val mainModule = module {
    includes(viewModelModule, useCaseModule, repoModule)
}
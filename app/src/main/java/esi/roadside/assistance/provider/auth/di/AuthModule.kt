package esi.roadside.assistance.provider.auth.di

import org.koin.dsl.module

val authModule = module {
    includes(RepoModule, useCaseModule, viewModelModule)
}
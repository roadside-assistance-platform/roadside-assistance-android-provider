package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.main.domain.use_cases.Logout
import esi.roadside.assistance.provider.main.domain.use_cases.SubmitRequest
import org.koin.dsl.module

val useCaseModule = module {
    factory { SubmitRequest(get()) }
    factory { Logout(get()) }
}
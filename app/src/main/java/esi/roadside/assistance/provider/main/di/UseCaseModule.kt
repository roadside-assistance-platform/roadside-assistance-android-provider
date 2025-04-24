package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.main.domain.use_cases.AcceptService
import esi.roadside.assistance.provider.main.domain.use_cases.DistanceCalculation
import esi.roadside.assistance.provider.main.domain.use_cases.Logout
import esi.roadside.assistance.provider.main.domain.use_cases.ReverseGeocoding
import org.koin.dsl.module

val useCaseModule = module {
    factory { ReverseGeocoding(get()) }
    factory { AcceptService(get()) }
    factory { DistanceCalculation(get()) }
    factory { Logout(get()) }
}
package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.main.presentation.MainViewModel
import esi.roadside.assistance.provider.main.presentation.routes.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(get(), get(), get())
    }
    viewModel {
        ProfileViewModel(get(), get(), get(), get())
    }
}
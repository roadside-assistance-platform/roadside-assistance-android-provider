package esi.roadside.assistance.provider.main.di

import esi.roadside.assistance.provider.main.presentation.MainViewModel
import esi.roadside.assistance.provider.main.presentation.routes.profile.ProfileViewModel
import esi.roadside.assistance.provider.main.presentation.routes.settings.ChangePasswordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(get(), get(), get(), get(), get())
    }
    viewModel {
        ProfileViewModel(get(), get(), get(), get(), get())
    }
    viewModel {
        ChangePasswordViewModel(get(), get())
    }
}
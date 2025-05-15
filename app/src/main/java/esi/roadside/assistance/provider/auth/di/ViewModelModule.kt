package esi.roadside.assistance.provider.auth.di

import esi.roadside.assistance.provider.auth.presentation.AuthViewModel
import esi.roadside.assistance.provider.auth.presentation.screens.login.LoginViewModel
import esi.roadside.assistance.provider.auth.presentation.screens.reset_password.ResetPasswordViewModel
import esi.roadside.assistance.provider.auth.presentation.screens.signup.SignupViewModel
import esi.roadside.assistance.provider.auth.util.account.AccountManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single {
        AccountManager(androidContext())
    }
    viewModel {
        AuthViewModel(get(), get())
    }
    viewModel {
        LoginViewModel(get(), get())
    }
    viewModel {
        SignupViewModel(get(), get(), get(), get(), get())
    }
    viewModel {
        ResetPasswordViewModel(get(), get(), get())
    }
}
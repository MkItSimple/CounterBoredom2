package com.mkitsimple.counterboredom2.di.modules

import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom2.ui.auth.AuthViewModel
import com.mkitsimple.counterboredom2.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    internal abstract fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel
}
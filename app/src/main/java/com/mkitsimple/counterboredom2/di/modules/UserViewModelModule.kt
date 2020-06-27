package com.mkitsimple.counterboredom2.di.modules

import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom2.viewmodels.UserViewModel
import com.mkitsimple.counterboredom2.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class UserViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    internal abstract fun bindUserViewModel(viewModel: UserViewModel): ViewModel
}
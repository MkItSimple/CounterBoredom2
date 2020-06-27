package com.mkitsimple.counterboredom2.di.modules

import androidx.lifecycle.ViewModelProvider
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
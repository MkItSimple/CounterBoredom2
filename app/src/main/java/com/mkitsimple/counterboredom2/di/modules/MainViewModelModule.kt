package com.mkitsimple.counterboredom2.di.modules

import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom2.ui.main.MainViewModel
import com.mkitsimple.counterboredom2.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}
package com.mkitsimple.counterboredom2.di.modules

import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom2.ui.main.FriendsListViewModel
import com.mkitsimple.counterboredom2.ui.main.LatestChatsViewModel
import com.mkitsimple.counterboredom2.ui.main.MainViewModel
import com.mkitsimple.counterboredom2.ui.main.ProfileViewModel
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

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    internal abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FriendsListViewModel::class)
    internal abstract fun bindFriendsListViewModel(viewModel: FriendsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LatestChatsViewModel::class)
    internal abstract fun bindLatestChatsViewModel(viewModel: LatestChatsViewModel): ViewModel
}
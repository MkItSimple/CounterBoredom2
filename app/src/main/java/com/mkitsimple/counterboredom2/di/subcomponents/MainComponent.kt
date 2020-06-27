package com.mkitsimple.counterboredom2.di.subcomponents

import com.mkitsimple.counterboredom2.di.modules.MainViewModelModule
import com.mkitsimple.counterboredom2.di.modules.ViewModelFactoryModule
import com.mkitsimple.counterboredom2.di.scopes.MainScope
import com.mkitsimple.counterboredom2.ui.main.MainActivity
import dagger.Subcomponent

@MainScope
@Subcomponent(
    modules = [
        ViewModelFactoryModule::class,
        MainViewModelModule::class
    ]
)
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}
package com.mkitsimple.counterboredom2.di.subcomponents

import com.mkitsimple.counterboredom2.di.modules.AuthViewModelModule
import com.mkitsimple.counterboredom2.di.modules.ViewModelFactoryModule
import com.mkitsimple.counterboredom2.di.scopes.AuthScope
import com.mkitsimple.counterboredom2.ui.auth.LoginActivity
import com.mkitsimple.counterboredom2.ui.auth.RegisterActivity
import dagger.Component
import dagger.Subcomponent

@AuthScope
@Subcomponent(
    modules = [
        ViewModelFactoryModule::class,
        AuthViewModelModule::class
    ]
)
interface AuthComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
}
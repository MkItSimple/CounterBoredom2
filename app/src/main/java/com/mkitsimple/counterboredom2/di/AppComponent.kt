package com.mkitsimple.counterboredom2.di

import com.mkitsimple.counterboredom2.di.modules.AppModule
import com.mkitsimple.counterboredom2.di.scopes.AppScope
import com.mkitsimple.counterboredom2.di.subcomponents.AuthComponent
import com.mkitsimple.counterboredom2.di.subcomponents.MainComponent
import dagger.Component

@AppScope
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {
    fun newMainComponent(): MainComponent
    fun newAuthComponent(): AuthComponent
    //fun newPokemonDetailsComponent(): PokemonDetailsComponent
}
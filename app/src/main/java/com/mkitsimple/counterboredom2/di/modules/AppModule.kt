package com.mkitsimple.counterboredom2.di.modules

import com.mkitsimple.counterboredom2.data.repositories.AuthRepository
import com.mkitsimple.counterboredom2.data.repositories.UserRepository
import com.mkitsimple.counterboredom2.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module
class AppModule {

//    @AppScope
//    @Provides
//    fun provideRepository(api: PokemonApi) = PokemonRepository(api)

    @AppScope
    @Provides
    fun provideAuthRepository() = AuthRepository()

    @AppScope
    @Provides
    fun provideUserRepository() = UserRepository()
}
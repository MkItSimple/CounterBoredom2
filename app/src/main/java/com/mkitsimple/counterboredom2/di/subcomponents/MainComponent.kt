package com.mkitsimple.counterboredom2.di.subcomponents

import com.mkitsimple.counterboredom2.di.modules.MainViewModelModule
import com.mkitsimple.counterboredom2.di.modules.ViewModelFactoryModule
import com.mkitsimple.counterboredom2.di.scopes.MainScope
import com.mkitsimple.counterboredom2.ui.main.FriendsListFragment
import com.mkitsimple.counterboredom2.ui.main.LatestChatsFragment
import com.mkitsimple.counterboredom2.ui.main.MainActivity
import com.mkitsimple.counterboredom2.ui.main.ProfileActivity
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
    fun inject(profileActivity: ProfileActivity)
    fun inject(friendsListFragment: FriendsListFragment)
    fun inject(latestChatsFragment: LatestChatsFragment)
}
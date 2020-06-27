package com.mkitsimple.counterboredom2

import android.app.Application
import com.mkitsimple.counterboredom2.di.AppComponent
import com.mkitsimple.counterboredom2.di.DaggerAppComponent

class BaseApplication : Application(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        this.appComponent = this.initDagger()
    }

    private fun initDagger()  = DaggerAppComponent.builder()
        .build()
}
package com.yanbin.ptpsample

import android.app.Application
import com.yanbin.ptpsample.di.ApplicationComponent
import com.yanbin.ptpsample.di.ApplicationModule
import com.yanbin.ptpsample.di.DaggerApplicationComponent
import timber.log.Timber

class MainApplication: Application() {

    private val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

    fun getComponent(): ApplicationComponent {
        return applicationComponent
    }
}
package com.example.locationproject.location_project

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MapApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android context
            androidContext(applicationContext)
            // modules
            modules(ModulesMap().createModules(applicationContext))
        }

    }

}
package com.rahul.notetaking.app

import android.app.Application
import com.rahul.notetaking.BuildConfig
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
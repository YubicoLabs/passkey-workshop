package io.yubicolabs.pawskey

import android.app.Activity
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PawskeyApplication : Application() {
    companion object {
        private lateinit var application: PawskeyApplication

        val instance: PawskeyApplication
            get() = application
    }

    lateinit var mainActivity: MainActivity

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}

package com.chanchhanun.biometricandroid

import android.app.Application
import com.chanchhanun.biometricandroid.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BiometricApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin errors and operations
            androidLogger()
            
            // Inject the Android Context so modules can use it if needed
            androidContext(this@BiometricApp)
            
            // Load your dependency injection module(s)
            modules(appModule)
        }
    }
}
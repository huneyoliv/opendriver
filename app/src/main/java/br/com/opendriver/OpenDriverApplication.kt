package br.com.opendriver

import android.app.Application
import br.com.opendriver.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class OpenDriverApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@OpenDriverApplication)
            modules(appModule)
        }
    }
}

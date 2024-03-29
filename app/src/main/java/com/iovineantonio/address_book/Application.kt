package com.iovineantonio.address_book

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.iovineantonio.address_book.di.DebugFlag
import com.iovineantonio.address_book.di.androidComponents
import com.iovineantonio.address_book.di.appComponents
import com.iovineantonio.address_book.di.databaseModule
import com.iovineantonio.address_book.di.viewModels
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.util.Locale
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import timber.log.Timber

const val TAG_LOGGING = "Address_Book"

class Application : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        setupLogging()
        setupDI()

        AndroidThreeTen.init(this)
        RxJavaPlugins.setErrorHandler { Timber.e(it) }
    }

    @Suppress("LongMethod")
    private fun setupDI() {
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@Application)

            @Suppress("USELESS_CAST")
            val appSetupModule = module {
                single { BuildConfig.DEBUG as DebugFlag }
            }

            modules(listOf(appSetupModule, appComponents, androidComponents, databaseModule, viewModels))
        }
    }

    private fun setupLogging() {
        val timberTag = TAG_LOGGING + ": " + BuildConfig.BUILD_TYPE
        Timber.plant(ExplicitDebugTree(BuildConfig.BUILD_TYPE))
        Timber.tag(timberTag)
    }

    private class ExplicitDebugTree(val buildConfig: String) : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
            return String.format(
                Locale.US,
                "(%s:%s) [%s()]",
                buildConfig,
                element.fileName,
                element.lineNumber,
                element.methodName
            )
        }
    }

    fun checkAppIsInRelease(): Boolean = BuildConfig.BUILD_TYPE.equals(other = "release", ignoreCase = true)
}

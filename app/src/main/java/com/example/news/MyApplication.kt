package com.example.news

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.news.data.cache.impl.ArticlesDatabase
import com.facebook.stetho.Stetho

/**
 * Extend the Application to hold the cache database instance, setup Stetho and manage Dark mode
 */
class MyApplication : Application() {

    // singleton database instance
    val database: ArticlesDatabase
        get() = ArticlesDatabase.getInstance(this)

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

    // support for Dark Mode
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Configuration.UI_MODE_NIGHT_YES ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}

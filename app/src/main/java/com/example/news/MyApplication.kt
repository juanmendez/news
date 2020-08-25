package com.example.news

import android.app.Application
import com.example.news.model.cache.ArticlesDatabase

class MyApplication : Application() {

    // singleton database instance
    val database: ArticlesDatabase
        get() = ArticlesDatabase.getInstance(this)
}

package com.example.news.util

import com.example.news.MyApplication
import com.example.news.model.Repository
import com.example.news.model.RepositoryImpl
import com.example.news.model.cache.ArticlesCacheService
import com.example.news.model.cache.ArticlesCacheServiceImpl
import com.example.news.model.cache.ArticlesDatabase
import com.example.news.model.network.ArticlesApiService
import com.example.news.model.network.ArticlesApiServiceImpl

object InjectorUtil {

    fun provideDatabase(application: MyApplication): ArticlesDatabase {
        return application.database
    }

    fun provideArticlesDaoService(application: MyApplication): ArticlesCacheService {
        val database = provideDatabase(application)
        return ArticlesCacheServiceImpl(database.articlesDao())
    }

    fun provideArticlesApiService(): ArticlesApiService {
        return ArticlesApiServiceImpl()
    }

    fun provideRepository(application: MyApplication): Repository {
        val articlesApiService = provideArticlesApiService()
        val articlesDaoService = provideArticlesDaoService(application)
        return RepositoryImpl(articlesApiService, articlesDaoService)
    }
}

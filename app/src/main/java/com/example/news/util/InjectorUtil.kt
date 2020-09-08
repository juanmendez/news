package com.example.news.util

import com.example.news.MyApplication
import com.example.news.model.Repository
import com.example.news.model.RepositoryImpl
import com.example.news.model.cache.ArticlesCacheService
import com.example.news.model.cache.impl.ArticlesCacheServiceImpl
import com.example.news.model.cache.impl.ArticlesDatabase
import com.example.news.model.network.ArticlesApiService
import com.example.news.model.network.impl.ArticlesApi
import com.example.news.model.network.impl.ArticlesApiServiceImpl
import com.example.news.model.network.impl.Network
import retrofit2.Retrofit

object InjectorUtil {

    fun provideDatabase(application: MyApplication): ArticlesDatabase {
        return application.database
    }

    fun provideArticlesDaoService(application: MyApplication): ArticlesCacheService {
        val database = provideDatabase(application)
        return ArticlesCacheServiceImpl(database.articlesDao())
    }

    fun provideRetrofit(): Retrofit {
        return Network.retrofit
    }

    fun provideArticlesApi(): ArticlesApi {
        return Network.articlesApi
    }

    fun provideArticlesApiService(): ArticlesApiService {
        val retrofit = provideRetrofit()
        val articlesApi = provideArticlesApi()
        return ArticlesApiServiceImpl(retrofit, articlesApi)
    }

    fun provideRepository(application: MyApplication): Repository {
        val articlesApiService = provideArticlesApiService()
        val articlesDaoService = provideArticlesDaoService(application)
        return RepositoryImpl(articlesApiService, articlesDaoService)
    }
}

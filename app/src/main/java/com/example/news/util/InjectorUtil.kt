package com.example.news.util

import com.example.news.MyApplication
import com.example.news.model.Repository
import com.example.news.model.Repository2
import com.example.news.model.Repository2Impl
import com.example.news.model.RepositoryImpl
import com.example.news.model.cache.CacheService
import com.example.news.model.cache.impl.CacheServiceImpl
import com.example.news.model.cache.impl.ArticlesDatabase
import com.example.news.model.cache.impl.CacheMapper
import com.example.news.model.network.ApiService
import com.example.news.model.network.ApiService2
import com.example.news.model.network.impl.Api
import com.example.news.model.network.impl.ApiService2Impl
import com.example.news.model.network.impl.ApiServiceImpl
import com.example.news.model.network.impl.Network
import retrofit2.Retrofit
import java.text.SimpleDateFormat

object InjectorUtil {

    fun provideDatabase(application: MyApplication): ArticlesDatabase {
        return application.database
    }

    fun provideCacheMapper(): CacheMapper {
        // https://developer.android.com/reference/java/text/SimpleDateFormat
        // "2020-08-24T14:38:37Z"
        // "August 08, 2020"
        return CacheMapper(
            dateUtil = DateUtil(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                SimpleDateFormat("MMMM dd',' YYYY")
            )
        )
    }

    fun provideCacheService(application: MyApplication): CacheService {
        val database = provideDatabase(application)
        val mapper = provideCacheMapper()
        return CacheServiceImpl(database.articlesDao(), mapper)
    }

    fun provideRetrofit(): Retrofit {
        return Network.retrofit
    }

    fun provideApi(): Api {
        return Network.API
    }

    fun provideApiService(): ApiService {
        val retrofit = provideRetrofit()
        val articlesApi = provideApi()
        return ApiServiceImpl(retrofit, articlesApi)
    }

    fun provideApiService2(): ApiService2 {
        val articlesApi = provideApi()
        return ApiService2Impl(articlesApi)
    }

    fun provideRepository(application: MyApplication): Repository {
        val articlesApiService = provideApiService()
        val articlesDaoService = provideCacheService(application)
        return RepositoryImpl(articlesApiService, articlesDaoService)
    }

    fun provideRepository2(application: MyApplication): Repository2 {
        val articlesApiService2 = provideApiService2()
        val articlesDaoService = provideCacheService(application)
        return Repository2Impl(articlesApiService2, articlesDaoService)
    }
}

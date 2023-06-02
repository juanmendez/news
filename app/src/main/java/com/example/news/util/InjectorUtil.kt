package com.example.news.util

import com.example.news.MyApplication
import com.example.news.data.cache.CacheService
import com.example.news.data.cache.impl.ArticlesDatabase
import com.example.news.data.cache.impl.CacheMapper
import com.example.news.data.cache.impl.CacheServiceImpl
import com.example.news.data.util.DateUtil
import com.example.news.network.Repository
import com.example.news.network.Repository2
import com.example.news.network.Repository2Impl
import com.example.news.network.Repository3
import com.example.news.network.Repository3Impl
import com.example.news.network.RepositoryImpl
import com.example.news.network.api.ApiService
import com.example.news.network.api.ApiService2
import com.example.news.network.api.ApiService3
import com.example.news.network.api.impl.Api
import com.example.news.network.api.impl.ApiService2Impl
import com.example.news.network.api.impl.ApiService3Impl
import com.example.news.network.api.impl.ApiServiceImpl
import com.example.news.network.api.impl.Network
import retrofit2.Retrofit
import java.text.SimpleDateFormat

/**
 * Injector singleton used to provide various object instances
 * (in lieu of a dependency injection framework)
 */
object InjectorUtil {

    fun provideDatabase(application: MyApplication): ArticlesDatabase = application.database

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

    fun provideRetrofit(): Retrofit = Network.retrofit

    fun provideApi(): Api = Network.API

    fun provideApiService(): ApiService {
        val retrofit = provideRetrofit()
        val articlesApi = provideApi()
        return ApiServiceImpl(retrofit, articlesApi)
    }

    fun provideApiService2(): ApiService2 {
        val articlesApi = provideApi()
        return ApiService2Impl(articlesApi)
    }

    fun provideApiService3(): ApiService3 {
        val articlesApi = provideApi()
        return ApiService3Impl(articlesApi)
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

    fun provideRepository3(application: MyApplication): Repository3 {
        val articlesApiService3 = provideApiService3()
        val articlesDaoService = provideCacheService(application)
        return Repository3Impl(articlesApiService3, articlesDaoService)
    }
}

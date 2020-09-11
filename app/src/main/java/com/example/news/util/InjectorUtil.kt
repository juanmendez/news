package com.example.news.util

import com.example.news.MyApplication
import com.example.news.model.Repository
import com.example.news.model.Repository2
import com.example.news.model.Repository2Impl
import com.example.news.model.RepositoryImpl
import com.example.news.model.cache.ArticlesCacheService
import com.example.news.model.cache.impl.ArticlesCacheServiceImpl
import com.example.news.model.cache.impl.ArticlesDatabase
import com.example.news.model.cache.impl.CacheMapper
import com.example.news.model.network.ArticlesApiService
import com.example.news.model.network.ArticlesApiService2
import com.example.news.model.network.impl.ArticlesApi
import com.example.news.model.network.impl.ArticlesApiService2Impl
import com.example.news.model.network.impl.ArticlesApiServiceImpl
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

    fun provideArticlesDaoService(application: MyApplication): ArticlesCacheService {
        val database = provideDatabase(application)
        val mapper = provideCacheMapper()
        return ArticlesCacheServiceImpl(database.articlesDao(), mapper)
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

    fun provideArticlesApiService2(): ArticlesApiService2 {
        val articlesApi = provideArticlesApi()
        return ArticlesApiService2Impl(articlesApi)
    }

    fun provideRepository(application: MyApplication): Repository {
        val articlesApiService = provideArticlesApiService()
        val articlesDaoService = provideArticlesDaoService(application)
        return RepositoryImpl(articlesApiService, articlesDaoService)
    }

    fun provideRepository2(application: MyApplication): Repository2 {
        val articlesApiService2 = provideArticlesApiService2()
        val articlesDaoService = provideArticlesDaoService(application)
        return Repository2Impl(articlesApiService2, articlesDaoService)
    }
}

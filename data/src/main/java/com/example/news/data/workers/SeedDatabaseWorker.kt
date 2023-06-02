package com.example.news.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.news.data.cache.impl.ArticleEntity
import com.example.news.data.cache.impl.ArticlesDatabase
import com.example.news.data.util.ARTICLES_DATA_FILENAME
import com.example.news.data.util.log

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.coroutineScope

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(ARTICLES_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val articleType = object : TypeToken<List<ArticleEntity>>() {}.type
                    val articles: List<ArticleEntity> = Gson().fromJson(jsonReader, articleType)

                    val database = ArticlesDatabase.getInstance(applicationContext)
                    database.articlesDao().insertArticles(articles)

                    Result.success()
                }
            }
        } catch (e: Exception) {
            log(TAG, "Error seeding database $e")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
    }
}

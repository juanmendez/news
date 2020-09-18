package com.example.news.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.news.model.cache.impl.ArticleEntity
import com.example.news.model.cache.impl.ArticlesDatabase
import com.example.news.util.ARTICLES_DATA_FILENAME
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
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
    }
}

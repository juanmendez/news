package com.example.news.model.cache.impl

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.news.workers.SeedDatabaseWorker

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
abstract class ArticlesDatabase : RoomDatabase() {

    /**
     * DAO accessor
     * At compile time the ArticlesDao interface implementation is generated and hooked to this
     * accessor method.
     */
    abstract fun articlesDao(): ArticlesDao

    companion object {
        @Volatile
        private var INSTANCE: ArticlesDatabase? = null

        /**
         * Database instance accessor
         */
        fun getInstance(context: Context): ArticlesDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticlesDatabase::class.java, "news.db"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build()
    }
}

package com.example.news.model.cache.impl

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArticleEntity::class], version = 1)
abstract class ArticlesDatabase : RoomDatabase() {

    abstract fun articlesDao(): ArticlesDao

    companion object {
        @Volatile
        private var INSTANCE: ArticlesDatabase? = null

        fun getInstance(context: Context): ArticlesDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticlesDatabase::class.java, "news.db"
            )
                .build()
    }
}

package com.example.news

import com.example.news.model.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ArticlesDataFactory(
    private val testClassLoader: ClassLoader
) {
    fun produceListOfArticles(): List<Article> {
        return Gson()
            .fromJson(
                getArticlesFromFile("articles.json"),
                object : TypeToken<List<Article>>() {}.type  // Gson stuff, don't ask
            )
    }

    fun produceHashMapOfArticles(noteList: List<Article>): HashMap<String, Article> {
        val map = HashMap<String, Article>()
        for (note in noteList) {
            map[note.id] = note
        }
        return map
    }

    fun produceEmptyListOfArticles(): List<Article> {
        return ArrayList()
    }

    // Outputs raw file data in a String. The class loader is used
    // as a context to get to the resources to get to the file
    private fun getArticlesFromFile(fileName: String): String {
        return testClassLoader.getResource(fileName).readText()
    }
}

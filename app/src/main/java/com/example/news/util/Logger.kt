package com.example.news.util

import android.util.Log
import com.example.news.BuildConfig.DEBUG
import com.example.news.model.Article

const val TAG = "News"

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

// In Unit Tests we cannot use Log.d, that's an Android API
// So in Unit Tests we will set isUnitTest to true and thus
// we will use println
var isUnitTest = false

fun log(className: String?, message: String) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    } else if (DEBUG && isUnitTest) {
        println("$className: $message")
    }
}

fun log3Articles(msg: String, articles: List<Article>) {
    log("toto", "$msg ${articles.size} articles:")
    for ((index, article) in articles.withIndex()) {
        log("toto", "${index + 1}. ${articles[index].title} | ${articles[index].publishedDate}")
        if (index == 2) break
    }
}

fun logArticles(msg: String, articles: List<Article>) {
    log("toto", "$msg ${articles.size} articles:")
    for ((index, article) in articles.withIndex()) {
        log("toto", "${index + 1}. ${articles[index].title} | ${articles[index].publishedDate}")
    }
}

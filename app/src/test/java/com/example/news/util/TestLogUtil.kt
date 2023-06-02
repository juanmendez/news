package com.example.news.util

import com.example.news.data.util.log

fun logObservedArticles(tag: String, articles: List<com.example.news.data.Article>) {
    log(tag, "observed count: ${articles.size}")
    for (article in articles) {
        log(tag, "observed title: ${article.title}")
    }
}

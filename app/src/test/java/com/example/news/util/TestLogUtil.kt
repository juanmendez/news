package com.example.news.util

import com.example.news.model.Article

fun logObservedArticles(tag: String, articles: List<Article>) {
    log(tag, "observed count: ${articles.size}")
    for (article in articles) {
        log(tag, "observed title: ${article.title}")
    }
}

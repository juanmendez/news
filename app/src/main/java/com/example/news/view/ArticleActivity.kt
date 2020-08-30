package com.example.news.view

import android.os.Bundle
import android.view.View
import com.example.news.R
import com.example.news.model.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article.*

class ArticleActivity : BaseActivity() {

    companion object {
        const val ARTICLE_EXTRA = "com.example.news.view.ArticleActivity.ARTICLE_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        if (intent.hasExtra(ARTICLE_EXTRA)) {
            val article = intent.getParcelableExtra<Article>(ARTICLE_EXTRA)
            if (article.imageUrl.isNotEmpty()) {
                article_image.visibility = View.VISIBLE
                Picasso.get()
                    .load(article.imageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .into(article_image)
            } else {
                article_image.visibility = View.GONE
            }
            article_source.text = article.sourceName
            article_date.text = article.publishedDate
            article_title.text = article.title
            if (article.content.isNullOrEmpty()) {
                article_content.visibility = View.GONE
            } else {
                article_content.visibility = View.VISIBLE
                article_content.text = article.content
            }
        }
    }
}

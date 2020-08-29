package com.example.news.view

import android.os.Bundle
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
            article_source.text = article.sourceName
            article_title.text = article.title
            article_date.text = article.publishedDate
            article_content.text = article.content
            if (article.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(article.imageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .into(article_image)
            }
        }
    }
}

package com.example.news.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.news.R
import com.example.news.model.Article
import kotlinx.android.synthetic.main.article_row.view.*
import java.util.*

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

class ArticlesAdapter(
    private val articles: List<Article>,
    private val listener: OnArticleClickListener,
    private val requestManager: RequestManager,
    private val viewPreloadSizeProvider: ViewPreloadSizeProvider<String>
) : RecyclerView.Adapter<ArticlesAdapter.ArticleHolder>(), ListPreloader.PreloadModelProvider<String> {

    /**
     * Implementing ListPreloader.PreloadModelProvider<String> interface for Glide RecyclerViewPreloader
     */
    override fun getPreloadItems(position: Int): MutableList<String> {
        with(articles[position].url) {
            return if (isNullOrBlank()) Collections.emptyList()
            else Collections.singletonList(this)
        }
    }

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? {
        return requestManager.load(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val v = parent.inflate(R.layout.article_row, false)
        return ArticleHolder(v, requestManager, viewPreloadSizeProvider)
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
        holder.itemView.setOnClickListener { listener.onArticleClick(article) }
    }

    override fun getItemCount() = articles.size

    // SRP (Single Responsibility Principle): A class should have only one reason to change.
    // Here we only do one thing: bind data to views.
    class ArticleHolder(
        v: View,
        private val requestManager: RequestManager,
        private val viewPreloadSizeProvider: ViewPreloadSizeProvider<String>
    ) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var article: Article? = null

        fun bind(article: Article) {
            this.article = article
            if (article.imageUrl.isNotEmpty()) {
                view.article_row_image.visibility = View.VISIBLE
                requestManager
                    .load(article.imageUrl)
                    .into(view.article_row_image)
                viewPreloadSizeProvider.setView(view.article_row_image)
            } else {
                view.article_row_image.visibility = View.GONE
                view.article_row_image.setImageDrawable(null)
            }
            view.article_row_source.text = article.sourceName
            view.article_row_date.text = article.publishedDate
            view.article_row_title.text = article.title
            if (article.content.isNullOrEmpty()) {
                view.article_row_content.visibility = View.GONE
            } else {
                view.article_row_content.visibility = View.VISIBLE
                view.article_row_content.text = article.content
            }
        }
    }
}

package com.example.news.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.model.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.article_row.view.*


fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

class ArticlesAdapter (private val articles: List<Article>,
                       private val listener: OnArticleClickListener
) : RecyclerView.Adapter<ArticlesAdapter.ArticleHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val v = parent.inflate(R.layout.article_row, false)
        return ArticleHolder(v)
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
        holder.itemView.setOnClickListener { listener.onArticleClick(article) }
    }

    override fun getItemCount() = articles.size

    class ArticleHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var article: Article? = null

        fun bind(article: Article) {
            this.article = article
            if (article.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(article.imageUrl)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(view.article_row_image)
            } else {
                view.article_row_image.setImageDrawable(null)
            }
            view.article_row_source.text = article.sourceName
            view.article_row_title.text = article.title
            view.article_row_date.text = article.publishedDate
        }
    }
}

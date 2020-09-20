package com.example.news.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.MyApplication
import com.example.news.R
import com.example.news.model.Article
import com.example.news.mvi.ArticleListStateEvent
import com.example.news.util.InjectorUtil
import com.example.news.util.TOP_HEADLINES
import com.example.news.viewmodel.ArticleListActivityViewModel3
import com.example.news.viewmodel.ArticleListActivityViewModel3Factory
import kotlinx.android.synthetic.main.activity_article_list.*

// same as ArticleListActivity, but MVI
class ArticleListActivity3 : BaseActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchMenu: MenuItem
    private lateinit var adapter: ArticlesAdapter
    private var articles: ArrayList<Article> = arrayListOf()

    private val listener = object : OnArticleClickListener {
        override fun onArticleClick(article: Article) {
            val intent = Intent(this@ArticleListActivity3, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.URL_EXTRA, article.url)
            this@ArticleListActivity3.startActivity(intent)
        }
    }

    private val viewModel3: ArticleListActivityViewModel3 by viewModels {
        val repository3 = InjectorUtil.provideRepository3(application as MyApplication)
        ArticleListActivityViewModel3Factory(repository3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)
        initUI()
        initObservers()
        saveQueryToRecentSuggestions(TOP_HEADLINES)
    }

    private fun initUI() {
        linearLayoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            else -> LinearLayoutManager(this)
        }
        articles_recyclerview.layoutManager = linearLayoutManager
        adapter = ArticlesAdapter(articles, listener)
        articles_recyclerview.adapter = adapter
    }

    private fun initObservers() {
        lifecycle.addObserver(viewModel3)

        // MVI: a single data state LiveData object contains the state of the View (Activity)
        viewModel3.dataState.observe(this, { dataState ->

            // update articles and query
            dataState.data?.let { event ->
                event.getContentIfNotHandled()?.let { articleListViewState ->
                    articleListViewState.articles?.let { articles ->
                        viewModel3.setArticlesData(articles)
                    }
                    articleListViewState.query?.let { query ->
                        viewModel3.setQueryData(query)
                    }
                }
            }

            // update progress bar
            showProgressBar(dataState.loading)

            // show error
            dataState.message?.let { event ->
                event.getContentIfNotHandled()?.let { message ->
                    showAlertDialog(message)
                }
            }
        })

        viewModel3.viewState.observe(this, { viewState ->
            viewState.articles?.let { data ->
                articles.clear()
                articles.addAll(data)
                adapter.notifyDataSetChanged()
            }
            viewState.query?.let { query ->
                supportActionBar?.title = query
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_article_list_menu, menu)
        searchMenu = menu.findItem(R.id.activity_article_list_menu_search)
        val searchView = searchMenu.actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
        }
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_SEARCH) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                saveQueryToRecentSuggestions(query)
                search(query)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when (item.itemId) {
            R.id.activity_article_list_menu_clear_history -> {
                SearchRecentSuggestions(
                    this,
                    ArticleSearchSuggestionProvider.AUTHORITY,
                    ArticleSearchSuggestionProvider.MODE
                )
                    .clearHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun search(query: String) {
        query?.let {
            this@ArticleListActivity3.hideKeyboard()
            searchMenu.collapseActionView()
            viewModel3.setStateEvent(ArticleListStateEvent.GetArticlesEvent(query))
        }
    }

    private fun saveQueryToRecentSuggestions(query: String) {
        SearchRecentSuggestions(
            this,
            ArticleSearchSuggestionProvider.AUTHORITY,
            ArticleSearchSuggestionProvider.MODE
        )
            .saveRecentQuery(query, null)
    }

    private fun showAlertDialog(message: String?) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setMessage(message ?: "Something went wrong...")
            .setCancelable(false)
            .setPositiveButton("Ok", null)
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}

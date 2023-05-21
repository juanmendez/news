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
import androidx.recyclerview.widget.RecyclerView
import com.example.news.MyApplication
import com.example.news.R
import com.example.news.databinding.ActivityArticleListBinding
import com.example.news.util.InjectorUtil
import com.example.news.util.TOP_HEADLINES
import com.example.news.view.WebViewActivity.Companion.URL_EXTRA
import com.example.news.viewmodel.ArticleListActivityViewModel2
import com.example.news.viewmodel.ArticleListActivityViewModel2Factory

/**
 * Same as ArticleListActivity, implements MVVM architecture.
 * Bundles state with data so that the ViewModel and the View do not need to handle the loading
 * state and the error state, merely displaying it.
 */
class ArticleListActivity2 : BaseActivity() {

    private lateinit var viewBinding: ActivityArticleListBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchMenu: MenuItem
    private lateinit var adapter: ArticlesAdapter
    private var articles: ArrayList<com.example.news.model.Article> = arrayListOf()

    private val listener = object : OnArticleClickListener {
        override fun onArticleClick(article: com.example.news.model.Article) {
            val intent = Intent(this@ArticleListActivity2, WebViewActivity::class.java)
            intent.putExtra(URL_EXTRA, article.url)
            this@ArticleListActivity2.startActivity(intent)
        }
    }

    // instantiates the ViewModel using a factory, constructor-injects the repository interface
    // implementation needed by the ViewModel instance, the repository needs the application context
    // for caching purposes
    private val viewModel2: ArticleListActivityViewModel2 by viewModels {
        val repository2 = InjectorUtil.provideRepository2(application as MyApplication)
        ArticleListActivityViewModel2Factory(repository2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityArticleListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
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
        viewBinding.articlesRecyclerview.layoutManager = linearLayoutManager
        // triggering loading new pages as we scroll to the end of the list
        val scrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (resources.configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE ->
                            if (!viewBinding.articlesRecyclerview.canScrollHorizontally(1)) {
                                viewModel2.incrementPage()
                            }
                        else ->
                            if (!viewBinding.articlesRecyclerview.canScrollVertically(1)) {
                                viewModel2.incrementPage()
                            }
                    }
                }
            }
        viewBinding.articlesRecyclerview.addOnScrollListener(scrollListener)
        adapter = ArticlesAdapter(articles, listener)
        viewBinding.articlesRecyclerview.adapter = adapter
        viewBinding.swipeRefresh.setOnRefreshListener {
            viewModel2.refresh()
        }
    }

    private fun initObservers() {
        // AppCompatActivity implements LifeCycleOwner single method interface and exposes it via
        // getLifecycle. By adding the ViewModel as an observer we allow the ViewModel to handle
        // the Activity's lifecycle events via annotations
        lifecycle.addObserver(viewModel2)

        // init data observers and update the UI
        viewModel2.articles.observe(this, { data ->
            articles.clear()
            articles.addAll(data)
            adapter.notifyDataSetChanged()
        })
        viewModel2.errorMessage.observe(this, { msg ->
            msg?.let {
                showAlertDialog(msg)
            }
        })
        viewModel2.trigger.observe(this, { trigger ->
            if (supportActionBar?.title != trigger.first) {
                // scroll to top if the query has changed
                viewBinding.articlesRecyclerview.scrollToPosition(0)
                supportActionBar?.title = trigger.first
            }
        })
        viewModel2.showProgress.observe(this, { show ->
            showProgressBar(show)
        })
        viewModel2.refreshing.observe(this, { refreshing ->
            if (refreshing == false) {
                viewBinding.swipeRefresh.isRefreshing = false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_article_list_menu, menu)
        searchMenu = menu.findItem(R.id.activity_article_list_menu_search)
        val searchView = searchMenu.actionView as SearchView

        // handle search via a search provider, making this activity the searchable activity
        // setup SearchView to send a search intent
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
        }
        return true
    }

    // receive and handle the search intent sent by the SearchView
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
        return when (item.itemId) {
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
        query.let {

            // update UI
            this@ArticleListActivity2.hideKeyboard()
            searchMenu.collapseActionView()

            // perform search
            viewModel2.setQuery(query)
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
            .setPositiveButton("Ok") { _, _ ->
                run {
                    // user dismissed the error dialog, clear the error message
                    viewModel2.showError(null)
                }
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}

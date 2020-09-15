package com.example.news.view

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
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
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.news.MyApplication
import com.example.news.R
import com.example.news.model.Article
import com.example.news.util.InjectorUtil
import com.example.news.view.WebViewActivity.Companion.URL_EXTRA
import com.example.news.viewmodel.ArticleListActivityViewModel2
import com.example.news.viewmodel.ArticleListActivityViewModel2.Companion.TOP_HEADLINES
import com.example.news.viewmodel.ArticleListActivityViewModel2Factory
import kotlinx.android.synthetic.main.activity_article_list.*

class ArticleListActivity2 : BaseActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchMenu: MenuItem
    private lateinit var adapter: ArticlesAdapter
    private var articles: ArrayList<Article> = arrayListOf()

    private val listener = object : OnArticleClickListener {
        override fun onArticleClick(article: Article) {
            val intent = Intent(this@ArticleListActivity2, WebViewActivity::class.java)
            intent.putExtra(URL_EXTRA, article.url)
            this@ArticleListActivity2.startActivity(intent)
        }
    }

    private val viewModel2: ArticleListActivityViewModel2 by viewModels {
        val repository2 = InjectorUtil.provideRepository2(application as MyApplication)
        ArticleListActivityViewModel2Factory(repository2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)
        initUI()
        initObservers()
        saveQueryToRecentSuggestions(TOP_HEADLINES)
    }

    /**
     * https://bumptech.github.io/glide/int/recyclerview.html
     * The RecyclerView integration library makes the RecyclerViewPreloader available in your
     * application. RecyclerViewPreloader can automatically load images just ahead of where a
     * user is scrolling in a RecyclerView. Combined with the right image size and an effective
     * disk cache strategy, this library can dramatically decrease the number of loading
     * tiles/indicators users see when scrolling through lists of images by ensuring that the
     * images the user is about to reach are already in memory.
     */
    private fun initUI() {

        linearLayoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            else -> LinearLayoutManager(this)
        }
        articles_recyclerview.layoutManager = linearLayoutManager

        // Glide request manager, used in the list adapter
        val requestManager = Glide.with(this).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.color.gray_light)
                .error(R.color.white)
        )

        // Glide Preloader
        val viewPreloadSizeProvider: ViewPreloadSizeProvider<String> = ViewPreloadSizeProvider()

        adapter = ArticlesAdapter(articles, listener, requestManager, viewPreloadSizeProvider)

        // Glide Preloader
        val recyclerViewPreloader: RecyclerViewPreloader<String> =
            RecyclerViewPreloader(
                Glide.with(this),
                adapter,
                viewPreloadSizeProvider,
                10
            )
        articles_recyclerview.addOnScrollListener(recyclerViewPreloader)

        // triggering loading new pages as we scroll to the bottom of the list
        val scrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (resources.configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE ->
                            if (!articles_recyclerview.canScrollHorizontally(1)) {
                                viewModel2.incrementPage()
                            }
                        else ->
                            if (!articles_recyclerview.canScrollVertically(1)) {
                                viewModel2.incrementPage()
                            }
                    }
                }
            }
        articles_recyclerview.addOnScrollListener(scrollListener)

        articles_recyclerview.adapter = adapter
    }

    private fun initObservers() {
        lifecycle.addObserver(viewModel2)

        // init data observers and update the UI
        viewModel2.articles.observe(this, { data ->
            //logArticles("Displaying", data)
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
                articles_recyclerview.scrollToPosition(0)
                supportActionBar?.title = trigger.first
            }
        })
        viewModel2.showProgress.observe(this, { show ->
            showProgressBar(show)
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
        query?.let {
            this@ArticleListActivity2.hideKeyboard()
            searchMenu.collapseActionView()
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
            .setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
                run {
                    viewModel2.showError(null)
                }
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}

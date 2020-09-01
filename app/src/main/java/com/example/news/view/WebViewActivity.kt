package com.example.news.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.news.R
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity : BaseActivity() {

    private lateinit var url: String

    companion object {
        const val URL_EXTRA = "com.example.news.view.ArticleActivity.URL_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        }

        if (intent.hasExtra(URL_EXTRA)) {
            url = intent.getStringExtra(URL_EXTRA)
            url?.let {
                web_view.settings.javaScriptEnabled = true
                web_view.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return false
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        showProgressBar(true)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        showProgressBar(false)
                    }
                }
                web_view.loadUrl(url)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_webview_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.activity_webview_menu_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                val shareIntent: Intent = Intent.createChooser(sendIntent, null)
                this@WebViewActivity.startActivity(shareIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

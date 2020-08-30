package com.example.news.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
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
                        showProgressBar()
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        hideProgressBar()
                    }
                }
                web_view.loadUrl(url)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_webview_menu, menu)
        val shareItem: MenuItem = menu.findItem(R.id.activity_webview_menu_share)
        val myShareActionProvider: ShareActionProvider =
            MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        val myShareIntent = Intent(Intent.ACTION_SEND)
        myShareIntent.type = "text/plain"
        myShareIntent.putExtra(Intent.EXTRA_TEXT, url)
        myShareActionProvider.setShareIntent(myShareIntent)
        return super.onCreateOptionsMenu(menu)
    }
}

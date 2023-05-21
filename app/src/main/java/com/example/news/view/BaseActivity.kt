package com.example.news.view

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.news.databinding.ActivityBaseBinding

/**
 * Base Activity class managing the progress bar.
 * Any subclass will inherit the progress bar functionality.
 */
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var baseViewBinding: ActivityBaseBinding
    private val progressBar: ProgressBar
        get() = baseViewBinding.progressBar

    override fun setContentView(view: View?) {
        baseViewBinding = ActivityBaseBinding.inflate(layoutInflater)
        baseViewBinding.activityContent.addView(view)

        super.setContentView(baseViewBinding.root)
    }

    fun showProgressBar(show: Boolean) {
        when (show) {
            true -> progressBar.visibility = View.VISIBLE
            false -> progressBar.visibility = View.GONE
        }
    }
}

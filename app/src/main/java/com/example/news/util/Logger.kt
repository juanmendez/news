package com.example.news.util

import android.util.Log
import com.example.news.BuildConfig.DEBUG

const val TAG = "News"

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

// In Unit Tests we cannot use Log.d, that's an Android API
// So in Unit Tests we will set isUnitTest to true and thus
// we will use println
var isUnitTest = false

fun log(className: String?, message: String) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    } else if (DEBUG && isUnitTest) {
        println("$className: $message")
    }
}

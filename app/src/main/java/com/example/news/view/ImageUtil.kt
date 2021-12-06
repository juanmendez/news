package com.example.news.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.news.R

const val DEFAULT_ARTICLE_IMAGE = R.mipmap.placeholder

@Composable
fun loadPicture(
    context: Context,
    url: String,
    @DrawableRes defaultImage: Int
): MutableState<Bitmap?> {

    val bitmapState: MutableState<Bitmap?> = remember {
        mutableStateOf(value = null, policy = structuralEqualityPolicy())
    }

    Glide.with(context)
        .asBitmap()
        .placeholder(defaultImage)
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })

    return bitmapState
}

package com.workfort.thinkndraw.util.helper

import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageLoader {

    fun load(url: Int, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }

    fun load(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }

    fun loadGif(url: Int, imageView: ImageView) {
        Glide.with(imageView.context)
            .asGif()
            .load(url)
            .into(imageView)
    }

}
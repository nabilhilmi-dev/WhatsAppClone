package com.nabil.whatsaapcloning.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.common.collect.ComparisonChain.start
import com.nabil.whatsaapcloning.R

fun populateImage(
    context: Context?,
    uri: String?,
    imageView: ImageView,
    errorDrawable: Int = R.drawable.empty
){
    if (context != null) {
        val options =
            RequestOptions().placeholder(progressDrawable(context)).error(errorDrawable)
        Glide.with(context).load(uri).apply(options).into(imageView)
    }

}

fun progressDrawable(context: Context): CircularProgressDrawable{
    return CircularProgressDrawable(context).apply{
        centerRadius = 5f
        strokeWidth = 5f
        start()


    }

}
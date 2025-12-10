package com.shoaib.demodatadog.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val author: String?,
    val content: String?,
    val sourceName: String?
) : Parcelable


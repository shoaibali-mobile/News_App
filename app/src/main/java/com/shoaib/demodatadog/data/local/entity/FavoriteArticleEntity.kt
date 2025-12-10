package com.shoaib.demodatadog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_articles")
data class FavoriteArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val author: String?,
    val content: String?,
    val sourceName: String?
)


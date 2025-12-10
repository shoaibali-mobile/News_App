package com.shoaib.demodatadog.domain.repository

import com.shoaib.demodatadog.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopHeadlines(country: String = "us", page: Int = 1): Result<List<Article>>
    suspend fun getHeadlinesByCategory(category: String, page: Int = 1): Result<List<Article>>
    suspend fun searchNews(query: String, page: Int = 1): Result<List<Article>>
    fun getFavorites(): Flow<List<Article>>
    suspend fun addToFavorites(article: Article)
    suspend fun removeFromFavorites(articleId: String)
    suspend fun isFavorite(articleId: String): Boolean
}


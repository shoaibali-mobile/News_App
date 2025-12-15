package com.shoaib.demodatadog.data.repository

import com.shoaib.demodatadog.data.local.dao.FavoriteDao
import com.shoaib.demodatadog.data.mapper.toArticle
import com.shoaib.demodatadog.data.mapper.toFavoriteEntity
import com.shoaib.demodatadog.data.remote.NewsApiService
import com.shoaib.demodatadog.domain.model.Article
import com.shoaib.demodatadog.domain.repository.NewsRepository
import com.shoaib.demodatadog.util.DatadogTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val favoriteDao: FavoriteDao,
    private val apiKey: String
) : NewsRepository {
    
    override suspend fun getTopHeadlines(country: String, page: Int): Result<List<Article>> {
        return try {
            val response = apiService.getTopHeadlines(apiKey, country, page = page)
            Result.success(response.articles.map { it.toArticle() })
        } catch (e: Exception) {
            // RUM tracking only (for user analytics)
            DatadogTracker.trackNetworkError(
                message = "Failed to load top headlines",
                throwable = e,
                attributes = mapOf(
                    "endpoint" to "top-headlines",
                    "country" to country,
                    "page" to page.toString()
                )
            )
            Result.failure(e)
        }
    }
    
    override suspend fun getHeadlinesByCategory(category: String, page: Int): Result<List<Article>> {
        return try {
            val response = apiService.getHeadlinesByCategory(apiKey, category, page = page)
            Result.success(response.articles.map { it.toArticle() })
        } catch (e: Exception) {
            DatadogTracker.trackNetworkError(
                message = "Failed to load headlines by category",
                throwable = e,
                attributes = mapOf(
                    "endpoint" to "headlines-by-category",
                    "category" to category,
                    "page" to page.toString()
                )
            )
            Result.failure(e)
        }
    }
    
    override suspend fun searchNews(query: String, page: Int): Result<List<Article>> {
        return try {
            val response = apiService.searchNews(apiKey, query, page = page)
            Result.success(response.articles.map { it.toArticle() })
        } catch (e: Exception) {
            DatadogTracker.trackNetworkError(
                message = "Failed to search news",
                throwable = e,
                attributes = mapOf(
                    "endpoint" to "search-news",
                    "query" to query,
                    "page" to page.toString()
                )
            )
            Result.failure(e)
        }
    }
    
    override fun getFavorites(): Flow<List<Article>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toArticle() }
        }
    }
    
    override suspend fun addToFavorites(article: Article) {
        favoriteDao.insertFavorite(article.toFavoriteEntity())
    }
    
    override suspend fun removeFromFavorites(articleId: String) {
        favoriteDao.deleteFavoriteById(articleId)
    }
    
    override suspend fun isFavorite(articleId: String): Boolean {
        return favoriteDao.getFavoriteById(articleId) != null
    }
}


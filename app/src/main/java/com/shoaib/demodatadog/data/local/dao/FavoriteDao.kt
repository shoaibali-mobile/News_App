package com.shoaib.demodatadog.data.local.dao

import androidx.room.*
import com.shoaib.demodatadog.data.local.entity.FavoriteArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_articles ORDER BY publishedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteArticleEntity>>
    
    @Query("SELECT * FROM favorite_articles WHERE id = :id")
    suspend fun getFavoriteById(id: String): FavoriteArticleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(article: FavoriteArticleEntity)
    
    @Delete
    suspend fun deleteFavorite(article: FavoriteArticleEntity)
    
    @Query("DELETE FROM favorite_articles WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
}


package com.shoaib.demodatadog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shoaib.demodatadog.data.local.dao.FavoriteDao
import com.shoaib.demodatadog.data.local.entity.FavoriteArticleEntity

@Database(entities = [FavoriteArticleEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}


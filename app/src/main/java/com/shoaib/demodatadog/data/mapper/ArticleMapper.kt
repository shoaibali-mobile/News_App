package com.shoaib.demodatadog.data.mapper

import com.shoaib.demodatadog.data.local.entity.FavoriteArticleEntity
import com.shoaib.demodatadog.data.remote.dto.ArticleDto
import com.shoaib.demodatadog.domain.model.Article

fun ArticleDto.toArticle(): Article {
    return Article(
        id = url.hashCode().toString(),
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        author = author,
        content = content,
        sourceName = source.name
    )
}

fun Article.toFavoriteEntity(): FavoriteArticleEntity {
    return FavoriteArticleEntity(
        id = id,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        author = author,
        content = content,
        sourceName = sourceName
    )
}

fun FavoriteArticleEntity.toArticle(): Article {
    return Article(
        id = id,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        author = author,
        content = content,
        sourceName = sourceName
    )
}


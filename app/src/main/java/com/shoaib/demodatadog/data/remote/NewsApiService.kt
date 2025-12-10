package com.shoaib.demodatadog.data.remote

import com.shoaib.demodatadog.data.remote.dto.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Header("X-Api-Key") apiKey: String,
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponse
    
    @GET("top-headlines")
    suspend fun getHeadlinesByCategory(
        @Header("X-Api-Key") apiKey: String,
        @Query("category") category: String,
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): NewsResponse
    
    @GET("everything")
    suspend fun searchNews(
        @Header("X-Api-Key") apiKey: String,
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en"
    ): NewsResponse
}


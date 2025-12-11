package com.shoaib.demodatadog.di

import com.datadog.android.okhttp.DatadogInterceptor
import com.datadog.android.okhttp.trace.TracingInterceptor
import com.shoaib.demodatadog.BuildConfig
import com.shoaib.demodatadog.data.remote.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private val tracedHosts = listOf(
        "newsapi.org",
        "api.newsapi.org"
    )
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        // for prod
        //level = HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
            .addInterceptor(
                DatadogInterceptor.Builder(tracedHosts)
                    .build()
            )
            .addNetworkInterceptor(
                TracingInterceptor.Builder(tracedHosts)
                    .build()
            )
            .addInterceptor(logging)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideApiKey(): String = BuildConfig.NEWS_API_KEY
}


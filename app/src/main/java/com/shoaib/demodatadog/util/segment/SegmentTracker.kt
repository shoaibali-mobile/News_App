package com.shoaib.demodatadog.util.segment

import com.segment.analytics.kotlin.core.Analytics
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Helper class for Segment Analytics tracking
object SegmentTracker {
    
    private var analytics: Analytics? = null
    private var userId: String? = null
    
    fun initialize(analyticsInstance: Analytics) {
        analytics = analyticsInstance
    }
    
    fun setUserId(id: String) {
        userId = id
        analytics?.identify(id)
    }
    
    fun identify(userId: String, traits: Map<String, Any> = emptyMap()) {
        this.userId = userId
        val traitsJson = buildJsonObject {
            traits.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                    else -> put(key, value.toString())
                }
            }
        }
        analytics?.identify(userId, traitsJson)
    }
    
    fun trackScreen(screenName: String, properties: Map<String, Any> = emptyMap()) {
        val allProperties = properties.toMutableMap().apply {
            userId?.let { put("user_id", it) }
        }
        val propertiesJson = buildJsonObject {
            allProperties.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                    else -> put(key, value.toString())
                }
            }
        }
        analytics?.screen(screenName, propertiesJson)
    }
    
    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap()) {
        val allProperties = properties.toMutableMap().apply {
            userId?.let { put("user_id", it) }
        }
        val propertiesJson = buildJsonObject {
            allProperties.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                    else -> put(key, value.toString())
                }
            }
        }
        analytics?.track(eventName, propertiesJson)
    }
    
    fun trackArticleViewed(articleId: String, articleTitle: String, source: String? = null) {
        trackEvent(
            "Article Viewed",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle,
                "article_source" to (source ?: "unknown")
            )
        )
    }
    
    fun trackArticleFavorited(articleId: String, articleTitle: String) {
        trackEvent(
            "Article Favorited",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle
            )
        )
    }
    
    fun trackArticleUnfavorited(articleId: String, articleTitle: String) {
        trackEvent(
            "Article Unfavorited",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle
            )
        )
    }
    
    fun trackArticleShared(articleId: String, articleTitle: String, shareMethod: String = "unknown") {
        trackEvent(
            "Article Shared",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle,
                "share_method" to shareMethod
            )
        )
    }
    
    fun trackSearchPerformed(query: String) {
        trackEvent(
            "Search Performed",
            mapOf(
                "search_query" to query,
                "query_length" to query.length
            )
        )
    }
    
    fun trackCategorySelected(category: String) {
        trackEvent(
            "Category Selected",
            mapOf("category" to category)
        )
    }
    
    fun trackButtonClick(buttonName: String, properties: Map<String, Any> = emptyMap()) {
        val allProperties = properties.toMutableMap().apply {
            put("button_name", buttonName)
        }
        trackEvent("Button Clicked", allProperties)
    }
    
    fun trackItemTap(itemName: String, properties: Map<String, Any> = emptyMap()) {
        val allProperties = properties.toMutableMap().apply {
            put("item_name", itemName)
        }
        trackEvent("Item Tapped", allProperties)
    }
    
    fun trackNavigation(fromScreen: String, toScreen: String, properties: Map<String, Any> = emptyMap()) {
        val allProperties = properties.toMutableMap().apply {
            put("from_screen", fromScreen)
            put("to_screen", toScreen)
        }
        trackEvent("Navigation", allProperties)
    }
    
    fun trackFragmentSelected(fragmentName: String, fragmentId: String? = null) {
        val properties = mutableMapOf<String, Any>(
            "fragment_name" to fragmentName
        )
        fragmentId?.let {
            properties["fragment_id"] = it
        }
        trackEvent("Fragment Selected", properties)
    }
    
    fun reset() {
        analytics?.reset()
        userId = null
    }
}

package com.shoaib.demodatadog.util

import com.datadog.android.rum.GlobalRumMonitor
import com.datadog.android.rum.RumActionType
import com.datadog.android.rum.RumErrorSource

// Helper class for manual Datadog RUM tracking
object DatadogTracker {

    private var userId: String = "Gohan349298290292"

    fun setUserId(id: String) {
        userId = id
    }

    fun startScreen(
        viewKey: String,
        screenName: String,
        attributes: Map<String, String> = emptyMap()
    ) {
        val allAttributes = attributes.toMutableMap().apply {
            put("user_id", userId)
            put("screen_type", "manual")
        }
        GlobalRumMonitor.get().startView(viewKey, screenName, allAttributes)
    }

    fun stopScreen(viewKey: String) {
        GlobalRumMonitor.get().stopView(viewKey)
    }

    fun trackAction(
        type: RumActionType,
        actionName: String,
        attributes: Map<String, String> = emptyMap()
    ) {
        val allAttributes = attributes.toMutableMap().apply {
            put("user_id", userId)
        }
        GlobalRumMonitor.get().addAction(type, actionName, allAttributes)
    }

    fun trackButtonClick(
        buttonName: String,
        attributes: Map<String, String> = emptyMap()
    ) {
        trackAction(RumActionType.CLICK, "button_clicked_$buttonName", attributes)
    }

    fun trackItemTap(
        itemName: String,
        attributes: Map<String, String> = emptyMap()
    ) {
        trackAction(RumActionType.TAP, "item_tapped_$itemName", attributes)
    }

    fun trackEvent(
        eventName: String,
        attributes: Map<String, String> = emptyMap()
    ) {
        val allAttributes = attributes.toMutableMap().apply {
            put("user_id", userId)
            put("event_type", "business")
        }
        GlobalRumMonitor.get().addAction(
            RumActionType.CUSTOM,
            eventName,
            allAttributes
        )
    }

    fun trackError(
        message: String,
        throwable: Throwable? = null,
        source: RumErrorSource = RumErrorSource.SOURCE,
        attributes: Map<String, String> = emptyMap()
    ) {
        val allAttributes = attributes.toMutableMap().apply {
            put("user_id", userId)
            throwable?.let {
                put("error_type", it.javaClass.simpleName)
            }
        }
        GlobalRumMonitor.get().addError(
            message = message,
            source = source,
            throwable = throwable,
            attributes = allAttributes
        )
    }

    fun trackNetworkError(
        message: String,
        throwable: Throwable? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        trackError(message, throwable, RumErrorSource.NETWORK, attributes)
    }

    fun trackTiming(timingName: String) {
        GlobalRumMonitor.get().addTiming(timingName)
    }

    fun trackArticleViewed(articleId: String, articleTitle: String, source: String? = null) {
        trackEvent(
            "article_viewed",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle,
                "article_source" to (source ?: "unknown")
            )
        )
    }

    fun trackArticleFavorited(articleId: String, articleTitle: String) {
        trackEvent(
            "article_favorited",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle
            )
        )
    }

    fun trackArticleUnfavorited(articleId: String, articleTitle: String) {
        trackEvent(
            "article_unfavorited",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle
            )
        )
    }

    fun trackArticleShared(articleId: String, articleTitle: String, shareMethod: String = "unknown") {
        trackEvent(
            "article_shared",
            mapOf(
                "article_id" to articleId,
                "article_title" to articleTitle,
                "share_method" to shareMethod
            )
        )
    }

    fun trackSearchPerformed(query: String) {
        trackEvent(
            "search_performed",
            mapOf(
                "search_query" to query,
                "query_length" to query.length.toString()
            )
        )
    }

    fun trackCategorySelected(category: String) {
        trackEvent(
            "category_selected",
            mapOf("category" to category)
        )
    }

    fun trackFragmentSelected(fragmentName: String, fragmentId: String? = null) {
        val attributes = mutableMapOf<String, String>(
            "fragment_name" to fragmentName
        )
        fragmentId?.let {
            attributes["fragment_id"] = it
        }
        trackEvent(
            "fragment_selected",
            attributes
        )
    }

    fun trackNavigation(fromScreen: String, toScreen: String, attributes: Map<String, String> = emptyMap()) {
        val allAttributes = attributes.toMutableMap().apply {
            put("from_screen", fromScreen)
            put("to_screen", toScreen)
        }
        trackAction(RumActionType.CUSTOM, "navigation", allAttributes)
    }
}

package com.shoaib.demodatadog.util

import com.datadog.android.log.Logger

/**
 * Singleton logger for Datadog Logs
 * Use this for debugging, troubleshooting, and detailed error context
 * 
 * Note: This is for LOGS (debugging info), not for tracking user actions.
 * For user action tracking, use DatadogTracker instead.
 */
object DatadogLogger {
    
    private var logger: Logger? = null
    
    /**
     * Initialize the logger instance
     * Call this once in Application.onCreate() after Logs.enable()
     */
    fun initialize(loggerInstance: Logger) {
        logger = loggerInstance
    }
    
    /**
     * Check if logger is initialized
     */
    fun isInitialized(): Boolean = logger != null
    
    /**
     * Log debug messages
     */
    fun d(message: String, throwable: Throwable? = null, attributes: Map<String, String> = emptyMap()) {
        logger?.d(message, throwable, attributes)
    }
    
    /**
     * Log info messages
     */
    fun i(message: String, throwable: Throwable? = null, attributes: Map<String, String> = emptyMap()) {
        logger?.i(message, throwable, attributes)
    }
    
    /**
     * Log warning messages
     */
    fun w(message: String, throwable: Throwable? = null, attributes: Map<String, String> = emptyMap()) {
        logger?.w(message, throwable, attributes)
    }
    
    /**
     * Log error messages
     */
    fun e(message: String, throwable: Throwable? = null, attributes: Map<String, String> = emptyMap()) {
        logger?.e(message, throwable, attributes)
    }
    
    /**
     * Log critical errors
     */
    fun wtf(message: String, throwable: Throwable? = null, attributes: Map<String, String> = emptyMap()) {
        logger?.wtf(message, throwable, attributes)
    }
}

package com.shoaib.demodatadog

import android.app.Application
import android.util.Log
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.log.Logs
import com.datadog.android.log.LogsConfiguration
import com.datadog.android.log.Logger
import android.app.Activity
import androidx.fragment.app.Fragment
import com.datadog.android.rum.Rum
import com.datadog.android.rum.RumConfiguration
import com.datadog.android.rum.tracking.ComponentPredicate
import com.datadog.android.rum.tracking.MixedViewTrackingStrategy
import com.datadog.android.trace.Trace
import com.datadog.android.trace.TraceConfiguration
import com.datadog.android.sessionreplay.SessionReplay
import com.datadog.android.sessionreplay.SessionReplayConfiguration
import com.shoaib.demodatadog.util.DatadogLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        initializeDatadog()
    }
    
    private fun initializeDatadog() {
        val clientToken = BuildConfig.DATADOG_CLIENT_TOKEN
        val applicationId = BuildConfig.DATADOG_APPLICATION_ID
        val env = BuildConfig.DATADOG_ENV
        val appVariantName = "" // Empty string since you don't have flavors
        
        if (clientToken.isNotEmpty() && applicationId.isNotEmpty()) {
            // Initialize Datadog SDK
            val configuration = Configuration.Builder(
                clientToken = clientToken,
                env = env,
                variant = appVariantName
            )
                .setFirstPartyHosts(listOf("newsapi.org", "api.newsapi.org"))
                .apply {
                    try {
                        useSite(DatadogSite.US1)
                    } catch (e: IllegalArgumentException) {
                        Log.e("Datadog", "Error setting site to US1", e)
                    }
                }
                .build()
            
            val trackingConsent = TrackingConsent.GRANTED
            Datadog.initialize(this, configuration, trackingConsent)
            
            // Set user information
            Datadog.setUserInfo(
                id = "Gohan349298290292",
                name = "Gohan",
                email = "Gohan@gmail.com"
            )
            
            // Initialize Session Replay
            val sessionReplayConfig = SessionReplayConfiguration.Builder(100f)
                .build()
            SessionReplay.enable(sessionReplayConfig)
            
            // Enable development logs
            Datadog.setVerbosity(Log.INFO)
            
            // Configure and enable Logs
            val logsConfig = LogsConfiguration.Builder().build()
            Logs.enable(logsConfig)
            
            // Create logger instance and initialize singleton
            val logger = Logger.Builder()
                .setNetworkInfoEnabled(true)
                .setLogcatLogsEnabled(true)
                .setName("AppLogger")
                .build()
            
            // Initialize the singleton logger
            DatadogLogger.initialize(logger)
            
            // Use singleton logger
            DatadogLogger.i("Datadog logging initialized!")
            
            // Initialize RUM with custom view name provider to show only simple class names
            val activityPredicate = object : ComponentPredicate<Activity> {
                override fun accept(component: Activity): Boolean = true
                override fun getViewName(component: Activity): String? {
                    return component.javaClass.simpleName
                }
            }
            
            val fragmentPredicate = object : ComponentPredicate<Fragment> {
                override fun accept(component: Fragment): Boolean = true
                override fun getViewName(component: Fragment): String? {
                    return component.javaClass.simpleName
                }
            }
            
            val rumConfiguration = RumConfiguration.Builder(applicationId)
                .useViewTrackingStrategy(
                    MixedViewTrackingStrategy(
                        trackExtras = true,
                        componentPredicate = activityPredicate,
                        supportFragmentComponentPredicate = fragmentPredicate
                    )
                )
                .build()
            Rum.enable(rumConfiguration)
            
            // Initialize Trace
            val traceConfig = TraceConfiguration.Builder()
                .build()
            Trace.enable(traceConfig)
        }
    }
}




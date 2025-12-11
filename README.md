# DemoDataDog - News App with Datadog Integration

A sample Android news application demonstrating comprehensive Datadog Real User Monitoring (RUM) integration with automatic instrumentation.

## 📱 Project Overview

This is a news application built with:
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture
- **Dependency Injection**: Dagger Hilt
- **Networking**: Retrofit + OkHttp
- **Database**: Room
- **UI**: Material Design with Navigation Component
- **Monitoring**: Datadog RUM with full automatic instrumentation

## 🎯 Datadog Integration

This app is fully instrumented with Datadog for automatic monitoring of:
- User interactions (taps, swipes, clicks)
- Network requests (API calls)
- Errors and crashes
- Session recordings (Session Replay)
- Performance metrics
- Distributed tracing

## 🚀 Automatic Instrumentation Setup

### What's Configured

#### 1. **Datadog SDK Initialization** (`NewsApplication.kt`)
- SDK initialized with client token and application ID
- Environment: `dev` (configurable via `local.properties`)
- Site: US1 (with error handling)
- First-party hosts: `newsapi.org`, `api.newsapi.org`
- User information: Set with ID `shoaib349298290292`

#### 2. **RUM (Real User Monitoring)**
- Enabled with `trackUserInteractions()` for automatic user interaction tracking
- Automatically tracks:
  - Screen views (Activities, Fragments)
  - User actions (taps, swipes, clicks)
  - View load times
  - Performance metrics

#### 3. **Network Tracking** (`NetworkModule.kt`)
- **DatadogInterceptor**: Tracks network requests for RUM
  - Automatically records all API calls
  - Captures request/response times, status codes, errors
- **TracingInterceptor**: Creates distributed traces
  - Links network requests to traces
  - Enables end-to-end tracing

#### 4. **Session Replay**
- Enabled with 100% sample rate
- Records complete user sessions
- Privacy masking enabled by default (text shown as "XXXXX")

#### 5. **Logs**
- Automatic log collection enabled
- Logger instance created with network info enabled
- Logcat integration for development

#### 6. **Traces**
- Distributed tracing enabled
- Automatically links network requests to traces

## 📦 Dependencies

All Datadog dependencies are managed in `gradle/libs.versions.toml`:

```toml
datadog = "3.3.0"

datadog-rum = "dd-sdk-android-rum"
datadog-logs = "dd-sdk-android-logs"
datadog-trace = "dd-sdk-android-trace"
datadog-okhttp = "dd-sdk-android-okhttp"
datadog-session-replay = "dd-sdk-android-session-replay"
```

## ⚙️ Configuration

### Required Credentials

Add the following to `local.properties`:

```properties
NEWS_API_KEY=your_news_api_key
DATADOG_CLIENT_TOKEN=your_datadog_client_token
DATADOG_APPLICATION_ID=your_datadog_application_id
DATADOG_ENV=dev
```

### BuildConfig Fields

The following fields are automatically generated from `local.properties`:
- `BuildConfig.NEWS_API_KEY`
- `BuildConfig.DATADOG_CLIENT_TOKEN`
- `BuildConfig.DATADOG_APPLICATION_ID`
- `BuildConfig.DATADOG_ENV`

## 🔧 Key Files

### `NewsApplication.kt`
- Main application class
- Initializes Datadog SDK
- Configures RUM, Logs, Traces, and Session Replay
- Sets user information

### `NetworkModule.kt`
- Dagger Hilt module for network configuration
- Configures OkHttpClient with Datadog interceptors
- Sets up Retrofit for API calls

## 📊 What Gets Tracked Automatically

### User Interactions
- All taps on buttons, cards, navigation items
- Swipes on RecyclerViews
- Screen navigation
- Floating action button clicks

### Network Requests
- All API calls to `newsapi.org`
- Request/response times
- Status codes (200, 404, 500, etc.)
- Network errors
- Request/response sizes

### Errors & Crashes
- Unhandled exceptions
- Crashes
- Network errors
- Stack traces with source mapping

### Performance Metrics
- Screen load times
- Time-to-Network-Settled (TNS)
- Interaction-to-Next-View (INV)
- Network request durations

### Session Replay
- Complete user session recordings
- Visual replay of user interactions
- Privacy masking (text shown as "XXXXX")

## 🔒 Security Considerations

### Current Setup
- **HttpLoggingInterceptor** is set to `Level.BODY` (logs full request/response)
- ⚠️ **Warning**: This may log sensitive data like JWT tokens, API keys, or user data

### Recommendations for Production
1. **Disable or reduce logging**:
   ```kotlin
   val logging = HttpLoggingInterceptor().apply {
       level = HttpLoggingInterceptor.Level.NONE  // For production
   }
   ```

2. **Use environment-based configuration**:
   ```kotlin
   val logLevel = if (BuildConfig.DEBUG) {
       HttpLoggingInterceptor.Level.BODY
   } else {
       HttpLoggingInterceptor.Level.NONE
   }
   ```

3. **Configure data scrubbing** in RUM configuration if needed

## 📈 Datadog Dashboard Features

Once the app is running, you can view in Datadog:

1. **RUM Explorer**: All user sessions with filters
2. **Session Replay**: Video-like playback of user sessions
3. **Error Tracking**: Grouped errors with stack traces
4. **Performance Monitoring**: Load times, Core Web Vitals
5. **Network Analysis**: API call performance and errors

## 🎯 Automatic Instrumentation Features

### What Works Automatically (No Manual Code Needed)

1. **View Tracking**: All Activities and Fragments are automatically tracked
2. **Action Tracking**: All user interactions (taps, swipes) are automatically captured
3. **Network Tracking**: All OkHttp requests are automatically monitored
4. **Error Tracking**: All crashes and exceptions are automatically reported
5. **Session Recording**: All user sessions are automatically recorded
6. **Performance Metrics**: Load times and performance data automatically collected

### Optional Advanced Features (Not Currently Implemented)

- Custom TNS threshold (default: 100ms)
- Custom INV threshold (default: 3 seconds)
- Manual view loading notifications
- Custom performance timings

These are optional and only needed for fine-tuning. The default automatic tracking works perfectly for most use cases.

## 🛠️ Architecture

```
app/
├── data/
│   ├── local/          # Room database entities
│   ├── remote/         # API DTOs and Retrofit service
│   └── repository/     # Repository implementation
├── domain/
│   ├── model/          # Domain models
│   └── repository/     # Repository interfaces
├── presentation/
│   ├── home/           # Home screen
│   ├── category/       # Category screen
│   ├── detail/         # Article detail screen
│   ├── favorites/      # Favorites screen
│   ├── search/         # Search screen
│   └── settings/       # Settings screen
└── di/                 # Dagger Hilt modules
    ├── NetworkModule.kt    # Network configuration with Datadog
    └── DatabaseModule.kt   # Room database configuration
```

## 📝 Notes

- **User ID**: Currently hardcoded as `shoaib349298290292` in `NewsApplication.kt`
- **Session Replay Privacy**: Text is masked by default (shows "XXXXX")
- **First-Party Hosts**: Configured for `newsapi.org` and `api.newsapi.org`
- **Environment**: Set to `dev` by default

## 🔗 Resources

- [Datadog Android SDK Documentation](https://docs.datadoghq.com/real_user_monitoring/mobile_and_tv_monitoring/android/)
- [RUM Setup Guide](https://docs.datadoghq.com/real_user_monitoring/mobile_and_tv_monitoring/android/setup/)
- [Session Replay](https://docs.datadoghq.com/real_user_monitoring/session_replay/)

## Checklist

- Datadog SDK initialized
- RUM enabled with automatic user interaction tracking
- Network interceptors configured (DatadogInterceptor + TracingInterceptor)
- Session Replay enabled
- Logs enabled
- Traces enabled
- First-party hosts configured
- User information set
- Error handling for site configuration
- ProGuard rules configured

## 🎉 Summary

This app is fully configured for **automatic instrumentation** with Datadog. All user interactions, network requests, errors, and sessions are automatically tracked without any manual code. Simply run the app and view the data in your Datadog dashboard!


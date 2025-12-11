# Datadog Integration Documentation

Complete guide to Datadog Real User Monitoring (RUM) integration with automatic instrumentation.

## 📊 Overview

This application is fully instrumented with Datadog for comprehensive monitoring of user interactions, network requests, errors, and performance metrics. All tracking is **automatic** - no manual instrumentation code required.

## 🎯 What is Real User Monitoring (RUM)?

Real User Monitoring (RUM) tracks real users using your app in real-time. It provides:

1. **Performance Monitoring**: Track how fast screens load, user actions, network requests
2. **Error Management**: Automatically catch crashes and errors, group them, show stack traces
3. **Analytics/Usage**: Understand who uses your app (country, device, OS), track user journeys
4. **Support/Troubleshooting**: Complete session recordings, see everything user did before error

## ✅ Automatic Instrumentation Features

### What's Automatically Tracked

#### 1. User Interactions
- ✅ All taps on buttons, cards, navigation items
- ✅ Swipes on RecyclerViews
- ✅ Screen navigation (Activities, Fragments)
- ✅ Floating action button clicks
- ✅ Bottom navigation clicks

#### 2. Network Requests
- ✅ All API calls to `newsapi.org`
- ✅ Request URL and method (GET, POST, etc.)
- ✅ Response status codes (200, 404, 500, etc.)
- ✅ Request/response times
- ✅ Network errors
- ✅ Request/response sizes
- ⚠️ **Note**: Request/response bodies are **NOT** tracked by default (for security)

#### 3. Errors & Crashes
- ✅ Unhandled exceptions
- ✅ Application crashes
- ✅ Network errors
- ✅ Stack traces with source mapping
- ✅ Error grouping and tracking over time

#### 4. Performance Metrics
- ✅ Screen load times
- ✅ Time-to-Network-Settled (TNS) - default 100ms threshold
- ✅ Interaction-to-Next-View (INV) - default 3 seconds threshold
- ✅ Network request durations
- ✅ View rendering times

#### 5. Session Replay
- ✅ Complete user session recordings
- ✅ Visual replay of user interactions
- ✅ Privacy masking enabled (text shown as "XXXXX" by default)
- ✅ 100% sample rate (all sessions recorded)

## 🔧 Configuration

### Files Involved

#### 1. `NewsApplication.kt`
Main application class where Datadog SDK is initialized.

**Key Components:**
- Datadog SDK initialization with Configuration
- First-party hosts configuration (`newsapi.org`, `api.newsapi.org`)
- RUM enabled with `trackUserInteractions()`
- Session Replay enabled (100% sample rate)
- Logs enabled
- Traces enabled
- User information set (`shoaib349298290292`)

#### 2. `NetworkModule.kt`
Dagger Hilt module for network configuration.

**Key Components:**
- `DatadogInterceptor`: Tracks network requests for RUM
- `TracingInterceptor`: Creates distributed traces
- Traced hosts: `newsapi.org`, `api.newsapi.org`

### Credentials Setup

Add to `local.properties`:
```properties
DATADOG_CLIENT_TOKEN=your_client_token
DATADOG_APPLICATION_ID=your_application_id
DATADOG_ENV=dev
```

These are automatically loaded into `BuildConfig` and used during initialization.

## 📦 Dependencies

All Datadog dependencies (version 3.3.0):

```kotlin
implementation("com.datadoghq:dd-sdk-android-rum:3.3.0")
implementation("com.datadoghq:dd-sdk-android-logs:3.3.0")
implementation("com.datadoghq:dd-sdk-android-trace:3.3.0")
implementation("com.datadoghq:dd-sdk-android-okhttp:3.3.0")
implementation("com.datadoghq:dd-sdk-android-session-replay:3.3.0")
```

## 🔍 How It Works

### Initialization Flow

1. **Application Starts** → `NewsApplication.onCreate()` called
2. **Datadog SDK Initialized** → Configuration built with credentials
3. **First-Party Hosts Set** → Tells Datadog which domains to trace
4. **RUM Enabled** → Automatic user interaction tracking starts
5. **Network Interceptors Added** → All OkHttp requests automatically tracked
6. **Session Replay Enabled** → All sessions automatically recorded

### Network Request Tracking

When your app makes an API call:

```
User Action → Retrofit → OkHttpClient
                              ↓
                    DatadogInterceptor (tracks for RUM)
                              ↓
                    TracingInterceptor (creates traces)
                              ↓
                    Network Request
                              ↓
                    Response tracked automatically
```

### What Gets Sent to Datadog

**For Each Network Request:**
- URL: `https://newsapi.org/v2/top-headlines`
- Method: `GET`
- Status Code: `200`
- Duration: `245ms`
- Size: `15.2 KB`
- Error (if any): Error message

**NOT Sent:**
- Request body content
- Response body content
- Headers (except tracing headers)

## 🔒 Security & Privacy

### Default Behavior

- ✅ Request/response bodies are **NOT** tracked by Datadog
- ✅ Only metadata is collected (URL, method, status, timing)
- ✅ Session Replay masks text by default ("XXXXX")
- ✅ User data is protected

### Important Note

Your `HttpLoggingInterceptor` with `Level.BODY` logs full request/response to **Logcat** (local device), but this is **separate** from Datadog. Datadog only receives metadata.

**For Production:**
```kotlin
// Disable body logging in production
val logging = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}
```

## 📈 Datadog Dashboard Features

Once the app is running, you can view in Datadog:

### 1. RUM Explorer
- All user sessions with filters
- View performance metrics
- Filter by user, device, country, etc.

### 2. Session Replay
- Video-like playback of user sessions
- See exactly what users did
- Privacy masking (text as "XXXXX")

### 3. Error Tracking
- Grouped errors with stack traces
- Error frequency over time
- Which users encountered errors
- "Replay Session" button to see what led to error

### 4. Performance Monitoring
- Screen load times
- Network request performance
- Core Web Vitals
- Performance trends

### 5. Network Analysis
- API call performance
- Request/response times
- Error rates
- Network errors

## 🎛️ Configuration Details

### First-Party Hosts

```kotlin
.setFirstPartyHosts(listOf("newsapi.org", "api.newsapi.org"))
```

**What it does:**
- Tells Datadog these are your API servers
- Enables detailed tracking for these domains
- Creates distributed traces for these requests
- Links network requests to user actions

### User Information

```kotlin
Datadog.setUserInfo(
    id = "shoaib349298290292",
    name = "Shoaib",
    email = "shoaibali26021999@gmail.com"
)
```

**What it does:**
- Links all events to this user ID
- Makes it easy to filter by user in Datadog
- Helps with support and troubleshooting

### Session Replay Privacy

By default, Session Replay masks text content (shows "XXXXX"). To show actual text:

```kotlin
val sessionReplayConfig = SessionReplayConfiguration.Builder(100f)
    .setImagePrivacy(ImagePrivacy.MASK_NONE)
    .setTextAndInputPrivacy(TextAndInputPrivacy.MASK_NONE)
    .setTouchPrivacy(TouchPrivacy.SHOW)
    .build()
```

**Note**: Only use this for development/testing. Keep masking enabled in production.

## 🔄 Network Interceptors Explained

### DatadogInterceptor

```kotlin
DatadogInterceptor.Builder(tracedHosts).build()
```

**Purpose**: Tracks network requests for RUM monitoring
- Records request metadata (URL, method, status, timing)
- Links requests to user sessions
- Captures errors
- **Does NOT** log request/response bodies

**Placement**: Application interceptor (runs before network call)

### TracingInterceptor

```kotlin
TracingInterceptor.Builder(tracedHosts).build()
```

**Purpose**: Creates distributed traces for APM
- Links network requests to traces
- Enables end-to-end tracing
- Connects frontend to backend

**Placement**: Network interceptor (runs after connection is made)

## 📊 Performance Metrics

### Automatic Metrics

1. **Loading Time**: Time for screen to load
2. **Time-to-Network-Settled (TNS)**: Time for all initial network calls to complete
   - Default threshold: 100ms (can be customized)
3. **Interaction-to-Next-View (INV)**: Time from last user action to next screen
   - Default threshold: 3 seconds (can be customized)

### Custom Metrics (Optional)

You can add custom performance timings:

```kotlin
GlobalRumMonitor.get().addTiming("hero_image_loaded")
```

This creates `@view.custom_timings.hero_image` in Datadog.

## 🐛 Error Tracking

### Automatic Error Tracking

- ✅ All unhandled exceptions
- ✅ Application crashes
- ✅ Network errors
- ✅ Stack traces with source mapping

### Error Grouping

Datadog automatically groups similar errors:
- Same error type + same message = same issue
- Shows first seen, last seen, total count
- Tracks across app versions

### Viewing Errors

In Datadog Error Tracking:
- See all errors grouped by issue
- Click on error to see details
- View stack trace
- See which users encountered it
- Click "Replay Session" to see what led to error

## 🎥 Session Replay

### What It Records

- Complete user session (up to 4 hours)
- All screen interactions
- All taps, swipes, clicks
- Screen transitions
- Visual representation of user journey

### Privacy

- Text content masked by default ("XXXXX")
- Images can be masked
- Sensitive inputs can be masked
- Configurable privacy settings

### Sample Rate

Currently set to 100% (all sessions recorded). Can be adjusted:

```kotlin
SessionReplayConfiguration.Builder(50f)  // 50% of sessions
```

## 🔗 Integration Points

### With Logs
- RUM events linked to logs
- View logs for specific user sessions
- Correlate errors with log entries

### With APM (Traces)
- Network requests linked to backend traces
- See full request path from app to backend
- Identify backend bottlenecks

### With Infrastructure
- Link app performance to infrastructure metrics
- See how server performance affects users

## 📝 Key Takeaways

1. **Fully Automatic**: No manual code needed for basic tracking
2. **Secure by Default**: Request/response bodies not tracked
3. **Privacy First**: Session Replay masks sensitive data
4. **Comprehensive**: Tracks interactions, network, errors, performance
5. **Easy Debugging**: Session replay shows exactly what users did

## 🚀 Getting Started

1. Add credentials to `local.properties`
2. Run the app
3. Perform some actions (navigate, tap, make API calls)
4. View data in Datadog dashboard:
   - Go to **Digital Experience** → **RUM & Session Replay**
   - See sessions, errors, performance metrics
   - Click on any session to replay it



Dashboard


Existing Dashboard:

<img width="1422" height="809" alt="Screenshot 2025-12-11 at 3 20 58 PM" src="https://github.com/user-attachments/assets/650eb8d2-0749-4766-a994-1f6f3d047182" />


Custom Dashboard

<img width="1427" height="815" alt="Custom Dashboard" src="https://github.com/user-attachments/assets/4698f7ac-bbd8-4e4a-9a6d-21d9eb62c094" />

RUM 


PERFORMANCE MONITORING :

<img width="1440" height="900" alt="Screenshot 2025-12-11 at 3 23 34 PM" src="https://github.com/user-attachments/assets/6c59c5af-ebd7-431f-b7f0-164012862819" />


Session Replay :
<img width="1440" height="900" alt="Session Replay" src="https://github.com/user-attachments/assets/1b8c0149-34f8-43d5-9a93-b55f1ec88e09" />

Error Tracking :

<img width="1440" height="900" alt="Error Tracking" src="https://github.com/user-attachments/assets/9259dd5e-7f4b-45d7-9e3f-5c967b16e632" />

We can fine-grained control over our  usage by allowing you to choose which errors should be included by Error Tracking.

<img width="1440" height="900" alt="Screenshot 2025-12-11 at 3 32 24 PM" src="https://github.com/user-attachments/assets/44bc01c2-dfe7-4da0-bb16-bc838f5adbd0" />


Alert Monitor

<img width="1440" height="900" alt="Alert Monitors" src="https://github.com/user-attachments/assets/7e30f3e5-ade7-46cb-a8a5-78edabceddee" />

Declare Inicdent

<img width="1415" height="782" alt="Declare Incident" src="https://github.com/user-attachments/assets/a50efaca-caba-4985-bcd4-6425652eff9c" />



Session Explorer:
<img width="1440" height="826" alt="Sessions Explorer" src="https://github.com/user-attachments/assets/1e9a02b8-bf1f-499c-b3c2-cbb63f6b5f91" />


Performanace Analytics

Users
![User](https://github.com/user-attachments/assets/1765468c-c744-4d61-80dd-29bf47286dc8)

<img width="1440" height="900" alt="Screenshot 2025-12-11 at 3 50 03 PM" src="https://github.com/user-attachments/assets/124c8c86-57c0-4100-9b33-4e75c6edd408" />



Engagement

<img width="1419" height="772" alt="Engagement" src="https://github.com/user-attachments/assets/628570c9-9bd7-4519-86f7-8e31ade790b2" />


Traffic

<img width="1440" height="900" alt="P Traffic" src="https://github.com/user-attachments/assets/5577fbd2-b6f0-44b6-ad39-ac97ff7370f2" />

   

## 📚 Additional Resources

- [Datadog Real User Monitoring (RUM) Documentation](https://docs.datadoghq.com/real_user_monitoring/)
- [Datadog Android SDK Documentation](https://docs.datadoghq.com/real_user_monitoring/mobile_and_tv_monitoring/android/)
- [RUM Setup Guide](https://docs.datadoghq.com/real_user_monitoring/mobile_and_tv_monitoring/android/setup/)
- [Session Replay Guide](https://docs.datadoghq.com/real_user_monitoring/session_replay/)
- [Error Tracking Guide](https://docs.datadoghq.com/real_user_monitoring/error_tracking/)





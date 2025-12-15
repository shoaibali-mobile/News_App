# Datadog Instrumentation: Theory and Best Practices

## Table of Contents

1. [Introduction](#introduction)
2. [Understanding Automatic Instrumentation](#understanding-automatic-instrumentation)
3. [Understanding Manual Instrumentation](#understanding-manual-instrumentation)
4. [Hybrid Approach: The Production Standard](#hybrid-approach-the-production-standard)
5. [Production-Level Best Practices](#production-level-best-practices)
6. [Decision Framework](#decision-framework)

---

## Introduction

Modern mobile applications require comprehensive monitoring to understand user behavior, diagnose issues, and optimize performance. Datadog provides two complementary approaches to instrumentation: **automatic** and **manual**. Understanding when and how to use each approach is crucial for building production-ready applications.

### The Instrumentation Spectrum

Instrumentation exists on a spectrum:
- **Pure Automatic:** SDK handles everything (ideal for standard events)
- **Hybrid Approach:** Automatic for standard events, manual for business logic (recommended for production)
- **Pure Manual:** Developer tracks everything (not recommended, high maintenance)

Most production applications benefit from a **hybrid approach**, leveraging automatic instrumentation for standard events while using manual tracking for business-specific insights.

---

## Understanding Automatic Instrumentation

### What is Automatic Instrumentation?

Automatic instrumentation refers to the SDK's ability to track application events **without requiring explicit tracking code** from developers. The Datadog SDK uses various techniques to intercept and monitor application behavior at the system level.

### How Automatic Instrumentation Works

#### 1. System-Level Interception

The SDK integrates deeply with the Android framework to intercept events:

- **View Tracking:** Uses Android's lifecycle callbacks (`onCreate`, `onStart`, `onResume`) to automatically detect when screens are displayed
- **Interaction Tracking:** Intercepts touch events at the system level before they reach your UI components
- **Network Tracking:** Uses OkHttp interceptors to monitor all network requests without modifying your API calls
- **Crash Detection:** Hooks into the Android exception handling system to capture unhandled exceptions

#### 2. Zero-Code Implementation

Once configured, automatic instrumentation requires **zero additional code** in your application logic. The SDK:

- Automatically tracks all Activities and Fragments
- Captures all user interactions (taps, swipes, clicks)
- Monitors all network requests made through configured HTTP clients
- Records unhandled exceptions and crashes
- Measures performance metrics (load times, rendering, network latency)

#### 3. Configuration-Based Customization

While automatic, the SDK allows customization through configuration:

- **View Name Strategy:** Customize how screens are named (e.g., simple class names vs full package paths)
- **First-Party Hosts:** Specify which domains should be tracked for network requests
- **Sampling Rates:** Control how much data is collected (important for cost management)
- **Privacy Settings:** Configure what data is masked or excluded

### Why Use Automatic Instrumentation?

#### Advantages

1. **Zero Maintenance Overhead**
   - No need to add tracking code in every component
   - SDK updates automatically improve tracking capabilities
   - Reduces risk of forgetting to track important events

2. **Comprehensive Coverage**
   - Captures events you might forget to track manually
   - Ensures consistent tracking across the entire application
   - No gaps in monitoring coverage

3. **Performance Optimized**
   - SDK-level optimizations minimize performance impact
   - Efficient data collection and batching
   - Minimal memory and battery usage

4. **Standardization**
   - Consistent event naming and structure
   - Follows industry best practices
   - Easy to understand and analyze

#### Limitations

1. **Generic Event Names**
   - Events are named based on UI components (e.g., "Button clicked")
   - Not business-meaningful (e.g., "Add to cart button clicked")

2. **Limited Business Context**
   - Cannot automatically understand business logic
   - Missing domain-specific attributes (e.g., product ID, category, user segment)

3. **Handled Errors Not Tracked**
   - Only captures unhandled exceptions
   - Errors caught in try-catch blocks require manual tracking

4. **No Business Event Detection**
   - Cannot automatically detect domain events (e.g., "purchase completed", "article favorited")
   - Requires manual tracking for business metrics

### What Gets Tracked Automatically?

#### Screen/View Tracking
- All Activities and Fragments are automatically tracked
- View load times and durations
- View transitions and navigation flows
- Custom view names can be configured

#### User Interactions
- All taps, clicks, and swipes
- Button interactions
- Text input interactions
- RecyclerView scrolling and interactions
- Bottom navigation changes

#### Network Requests
- All HTTP/HTTPS requests through configured clients
- Request/response times
- Status codes and error responses
- Request/response sizes
- Network errors and failures

#### Errors and Crashes
- Unhandled exceptions
- Application crashes
- Stack traces with source mapping
- Error grouping and aggregation

#### Performance Metrics
- Screen load times
- Time-to-Network-Settled (TNS)
- Interaction-to-Next-View (INV)
- Network request durations
- View rendering performance

#### Session Replay
- Complete session recordings
- Visual replay of user interactions
- Privacy masking for sensitive data

---

## Understanding Manual Instrumentation

### What is Manual Instrumentation?

Manual instrumentation involves **explicitly adding tracking code** to capture business-specific events, add contextual information, and track handled errors. This approach gives developers full control over what is tracked and what attributes are included.

### How Manual Instrumentation Works

#### 1. Explicit Event Tracking

Developers call tracking methods at specific points in the application flow:

- **Business Events:** Track domain-specific actions (e.g., "article_viewed", "purchase_completed")
- **Custom Attributes:** Add business context (e.g., product ID, category, user segment)
- **Error Context:** Add relevant information when errors occur
- **User Journey:** Track navigation flows and user paths

#### 2. Two-Tier Architecture

Our application uses a two-tier manual tracking approach:

**RUM (Real User Monitoring) Tracking:**
- Used for user-facing events and errors
- Tracks business events that impact user experience
- Provides analytics and user behavior insights
- Example: Article viewed, favorited, shared

**Logs Tracking:**
- Used for debugging and troubleshooting
- Tracks detailed error context for developers
- Helps diagnose issues in ViewModels and business logic
- Example: ViewModel error with full stack trace and context

#### 3. Separation of Concerns

**Repository Layer:**
- Uses RUM tracking only
- Tracks user-facing errors (network failures, API errors)
- Includes business context (endpoint, query, category)
- **Why:** These errors directly impact users and need to be visible in analytics

**ViewModel Layer:**
- Uses Logs tracking only
- Tracks debugging information for developers
- Includes detailed context for troubleshooting
- **Why:** These are internal errors that need developer attention, not user analytics

**UI Layer:**
- Uses RUM tracking for business events
- Tracks user interactions with business context
- Captures navigation and user journey
- **Why:** These events represent user behavior and business metrics

### Why Use Manual Instrumentation?

#### Advantages

1. **Business Intelligence**
   - Track domain-specific events that matter to your business
   - Understand user behavior in business terms
   - Measure feature adoption and usage

2. **Rich Context**
   - Add business attributes to events (product ID, category, user segment)
   - Provide context for errors (what was the user trying to do?)
   - Enable powerful filtering and analysis in Datadog

3. **Handled Error Tracking**
   - Track errors that are caught and handled gracefully
   - Add business context to error reports
   - Understand error patterns in business terms

4. **Custom Analytics**
   - Track A/B test events
   - Measure feature usage
   - Understand user journeys and conversion funnels

#### Limitations

1. **Maintenance Overhead**
   - Requires adding tracking code throughout the application
   - Must remember to track new features
   - Risk of missing important events

2. **Code Duplication Risk**
   - Easy to accidentally track the same event multiple times
   - Can lead to inflated metrics
   - Requires careful code review

3. **Performance Considerations**
   - Each tracking call has a small overhead
   - Too many tracking calls can impact performance
   - Requires careful design and optimization

4. **Inconsistency Risk**
   - Different developers may track events differently
   - Event naming can become inconsistent
   - Requires clear guidelines and code review

### What Gets Tracked Manually?

#### Business Events
- Domain-specific actions (article viewed, favorited, shared)
- Feature usage (search performed, category selected)
- User journey events (navigation between screens)
- Conversion events (purchases, sign-ups, etc.)

#### Custom Screen Tracking
- Override automatic screen names with business-friendly labels
- Add contextual attributes to screen views
- Track screen-specific metrics

#### Handled Errors
- Network errors with business context
- Validation errors
- Business logic errors
- Errors with relevant attributes (what was the user doing?)

#### Debugging Information
- Detailed error logs for troubleshooting
- ViewModel state information
- Business logic flow tracking
- Performance timing information

---

## Hybrid Approach: The Production Standard

### Why Hybrid is the Best Approach

The **hybrid approach** combines automatic and manual instrumentation, leveraging the strengths of both while minimizing their weaknesses. This is the **recommended best practice** for production applications.

### The 70/30 Rule

In a well-designed hybrid approach:
- **~70% of events are tracked automatically** (standard events: views, interactions, network, crashes)
- **~30% of events are tracked manually** (business events, custom context, handled errors)

This ratio provides comprehensive coverage while maintaining code maintainability.

### How Hybrid Works in Practice

#### Automatic for Standard Events

The SDK automatically handles:
- All screen views and transitions
- All user interactions (taps, swipes, clicks)
- All network requests and responses
- All crashes and unhandled exceptions
- Performance metrics and timing

**Result:** Zero code needed for standard monitoring.

#### Manual for Business Logic

Developers explicitly track:
- Business events (purchases, favorites, shares)
- Custom attributes (product IDs, categories, user segments)
- Handled errors with business context
- User journey and navigation flows
- Feature usage and adoption

**Result:** Rich business intelligence and analytics.

### Benefits of Hybrid Approach

1. **Comprehensive Coverage**
   - Automatic tracking ensures no gaps in standard events
   - Manual tracking adds business-specific insights
   - Best of both worlds

2. **Maintainability**
   - Minimal manual code required
   - Focus manual tracking on high-value business events
   - SDK handles the rest automatically

3. **Business Intelligence**
   - Standard metrics from automatic tracking
   - Business insights from manual tracking
   - Complete picture of application health and user behavior

4. **Cost Efficiency**
   - Automatic tracking is optimized by SDK
   - Manual tracking adds value with minimal overhead
   - Efficient data collection and transmission

5. **Flexibility**
   - Can override automatic tracking when needed
   - Can add custom context to automatic events
   - Adaptable to changing requirements

### Hybrid Architecture Pattern

#### Layer-Based Separation

**Repository Layer (Data Layer):**
- **Automatic:** Network requests tracked via interceptors
- **Manual:** Network errors tracked with RUM (user-facing)
- **Why:** Errors here impact users directly

**ViewModel Layer (Business Logic):**
- **Automatic:** None (business logic layer)
- **Manual:** Errors tracked with Logs (debugging)
- **Why:** Internal errors need developer attention

**UI Layer (Presentation):**
- **Automatic:** Screen views, user interactions
- **Manual:** Business events, custom screen tracking
- **Why:** User behavior and business metrics

### Avoiding Duplication

A key principle in hybrid instrumentation is **avoiding duplication**:

- **Don't track the same event twice** (e.g., both automatic and manual)
- **Repository errors:** Track once with RUM (user impact)
- **ViewModel errors:** Track once with Logs (debugging)
- **UI events:** Use automatic for standard interactions, manual for business events

This ensures accurate metrics and prevents data inflation.

---

## Production-Level Best Practices

### 1. Sampling and Cost Management

#### Session Replay Sampling
- **Development:** 100% sampling (see everything)
- **Production:** 10-20% sampling (manage costs)
- **Why:** Session replay generates large amounts of data

#### Error Sampling
- Consider sampling for high-volume, low-severity errors
- Always track critical errors at 100%
- Use attributes to filter and prioritize

#### Network Request Sampling
- Automatic tracking is already optimized
- Consider filtering out health check endpoints
- Focus on first-party hosts for detailed tracking

### 2. Privacy and Security

#### Data Scrubbing
- Mask sensitive information (passwords, tokens, PII)
- Configure privacy settings in SDK
- Review attributes before sending to Datadog

#### User Consent
- Respect user privacy preferences
- Implement consent management
- Allow users to opt-out of tracking

#### Network Logging
- **Development:** Full request/response logging for debugging
- **Production:** Disable body logging, log headers only
- **Why:** Request bodies may contain sensitive data

### 3. Environment-Based Configuration

#### Development vs Production
- Different sampling rates per environment
- Different log levels (verbose in dev, errors in prod)
- Different session replay settings

#### Feature Flags
- Use feature flags to enable/disable tracking features
- A/B test tracking implementations
- Gradual rollout of new tracking

### 4. Error Tracking Strategy

#### User-Facing Errors (RUM)
- Track in Repository layer
- Include business context
- Visible in analytics dashboards
- Examples: Network failures, API errors

#### Developer Errors (Logs)
- Track in ViewModel layer
- Include debugging context
- Visible in logs for troubleshooting
- Examples: Business logic errors, state management issues

#### Critical Errors
- Track in both RUM and Logs
- Include full context
- Set up alerts
- Examples: Payment failures, data corruption

### 5. Performance Optimization

#### Minimize Tracking Overhead
- Batch tracking calls when possible
- Use async tracking (non-blocking)
- Avoid tracking in tight loops
- Profile tracking impact

#### Efficient Data Collection
- Only track necessary attributes
- Avoid large payloads in attributes
- Use appropriate data types
- Compress data when possible

### 6. Monitoring and Alerting

#### Key Metrics to Monitor
- Error rates by type and screen
- Performance metrics (load times, response times)
- Business event volumes
- User journey completion rates

#### Alert Configuration
- Set up alerts for critical errors
- Monitor error rate spikes
- Track performance degradation
- Business metric thresholds

### 7. Documentation and Guidelines

#### Team Guidelines
- Document what should be tracked automatically vs manually
- Establish naming conventions for manual events
- Create code review checklist for tracking
- Regular audits of tracking implementation

#### Event Catalog
- Maintain a catalog of all tracked events
- Document event attributes and meanings
- Version control for event schemas
- Deprecation process for old events

---

## Decision Framework

### When to Use Automatic Instrumentation

Use automatic instrumentation for:

**Standard UI Events**
- All taps, clicks, swipes
- Screen views and transitions
- Standard user interactions

**Network Monitoring**
- All API calls and responses
- Network errors and timeouts
- Request/response times

**System Events**
- Application crashes
- Unhandled exceptions
- Performance metrics

**Session Replay**
- Visual session recordings
- User interaction replays

**Reason:** These events are standard across all applications and don't require business context.

### When to Use Manual Instrumentation

Use manual instrumentation for:

**Business Events**
- Domain-specific actions (purchases, favorites, shares)
- Feature usage tracking
- Conversion events

**Business Context**
- Adding product IDs, categories, user segments
- Custom attributes for analysis
- Business-specific metadata

**Handled Errors**
- Errors caught in try-catch blocks
- Business logic errors
- Validation errors with context

**User Journey**
- Navigation flows
- Feature adoption
- Conversion funnels

**A/B Testing**
- Experiment events
- Variant tracking
- Feature flag usage

**Reason:** These events require business understanding and context that the SDK cannot automatically detect.

### Decision Tree

```
Is this a standard application event?
├─ YES → Use Automatic Instrumentation
│   └─ Examples: Screen view, button click, network request
│
└─ NO → Does it require business context?
    ├─ YES → Use Manual Instrumentation (RUM)
    │   └─ Examples: Article favorited, purchase completed
    │
    └─ NO → Is it a handled error for debugging?
        ├─ YES → Use Manual Instrumentation (Logs)
        │   └─ Examples: ViewModel errors, business logic issues
        │
        └─ NO → Re-evaluate: Should this be tracked?
```

### Common Mistakes to Avoid

**Tracking the Same Event Twice**
- Don't manually track events that are already automatic
- Example: Don't manually track button clicks if automatic tracking is enabled

**Missing Business Context**
- Don't track business events without relevant attributes
- Example: Track "article_viewed" with article_id, not just the event name

**Inconsistent Naming**
- Establish and follow naming conventions
- Example: Use "article_favorited" not "favorite_article" or "articleFavorite"

**Over-Tracking**
- Don't track every single interaction
- Focus on business-critical events
- Example: Track "search_performed" not every keystroke

**Under-Tracking**
- Don't forget to track important business events
- Review new features for tracking needs
- Example: Track "purchase_completed" with order details

---

## Conclusion

### Key Takeaways

1. **Automatic Instrumentation** provides zero-code tracking for standard events, ensuring comprehensive coverage with minimal maintenance.

2. **Manual Instrumentation** adds business intelligence and context, enabling powerful analytics and business insights.

3. **Hybrid Approach** is the production standard, combining the strengths of both approaches while minimizing weaknesses.

4. **Separation of Concerns** is critical: RUM for user-facing events, Logs for debugging, avoid duplication.

5. **Production Best Practices** include sampling, privacy, environment-based config, and performance optimization.

### The Production-Ready Formula

**For Production Applications:**
- Enable automatic instrumentation for standard events (~70%)
- Add manual tracking for business events (~30%)
- Use RUM for user-facing errors and business metrics
- Use Logs for debugging and troubleshooting
- Implement sampling and cost management
- Follow privacy and security best practices
- Monitor and optimize performance
- Document and maintain tracking guidelines

This hybrid approach provides comprehensive monitoring, actionable business insights, and maintainable code—the foundation of a production-ready application.

---

**Document Version:** 2.0  
**Focus:** Theoretical understanding and production best practices  
**Target Audience:** Developers, Architects, Product Managers

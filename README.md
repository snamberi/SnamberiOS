# Snamberi OS for Android

Android Studio project containing the existing offline Snamberi OS dashboard and native scheduled notifications.

## Build APK
1. Install Android Studio with Android SDK 35 and JDK 17.
2. Open this folder in Android Studio and allow Gradle sync.
3. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. Install the generated `app/build/outputs/apk/debug/app-debug.apk` on your Android phone.

## Notifications
On Android 13+, allow notifications when prompted. Configure times in **Settings > Phone notifications**. Daily skincare/bedtime and weekly basketball/gym reminders are local Android alarms, and calendar events with a date and time generate one-time notifications. Notifications can be delayed by Android power management. Boot receiver restores alarms after restart. All data stays on the device.

This is source code, not a precompiled APK. Android build tools are required to produce the APK.

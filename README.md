# Hotwire Native Android - Complete Setup Guide

## 📋 Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Initial Setup](#initial-setup)
- [Configuration](#configuration)
- [Common Issues & Solutions](#common-issues--solutions)
- [Running the App](#running-the-app)
- [Important Notes](#important-notes)

---

## Overview

This guide documents the complete setup process for creating a Hotwire Native Android application, including all the configuration changes and troubleshooting steps needed to avoid common issues.

**What is Hotwire Native?**
Hotwire Native allows you to wrap your Rails (or any web) application in a native Android app, providing native navigation and features while keeping your web-based UI.

**Official Documentation:** https://native.hotwired.dev/android/getting-started

---

## Prerequisites

- **Android Studio** (Latest version recommended)
- **JDK 11** or higher
- **Minimum SDK:** API 28 (Android 9.0)
- **Kotlin:** Version 2.1.0 or higher
- A running Rails server (or any web app you want to wrap)

## Initial Setup

### Step 1: Create New Android Project

1. Open Android Studio
2. **File** → **New** → **New Project**
3. Select **"Empty Views Activity"** template
4. Configure project:
   - **Name:** Your app name
   - **Package name:** com.example.yourapp
   - **Language:** Kotlin
   - **Minimum SDK:** API 28
   - **Build configuration language:** Kotlin DSL (build.gradle.kts)
5. Click **Finish**

## ⚙️ Configuration

### Step 2: Update Gradle Version Catalog

Open `gradle/libs.versions.toml` and update the versions:

```toml
[versions]
agp = "8.7.2"
kotlin = "2.1.0"  # CRITICAL: Must be 2.1.0 or higher for Hotwire Native 1.2.4+
coreKtx = "1.15.0"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"
appcompat = "1.7.0"
material = "1.12.0"
activity = "1.9.3"
constraintlayout = "2.2.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

**IMPORTANT:** Kotlin version must be 2.1.0 or higher, otherwise you'll get a compatibility error with Hotwire Native libraries.

---

### Step 3: Update app/build.gradle.kts

Open `app/build.gradle.kts` and configure it as follows:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.yourapp"  // Update to your package name
    compileSdk = 35  // MUST be 35 or higher for latest AndroidX dependencies
    
    defaultConfig {
        applicationId = "com.example.yourapp"
        minSdk = 28  // Required by Hotwire Native
        targetSdk = 35  // Match compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Hotwire Native dependencies
    // Check for latest version: https://github.com/hotwired/hotwire-native-android/releases
    implementation("dev.hotwire:core:1.2.4")
    implementation("dev.hotwire:navigation-fragments:1.2.4")
    
    // Standard Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

**Key Points:**
- `compileSdk = 35` - Required for AndroidX Core 1.16.0+
- `minSdk = 28` - Minimum required by Hotwire Native
- `buildFeatures { viewBinding = true }` - Enables View Binding
- Update Hotwire versions from the [releases page](https://github.com/hotwired/hotwire-native-android/releases)

---

### Step 4: Update AndroidManifest.xml

Open `app/src/main/AndroidManifest.xml` and add internet permission **above** the `<application>` tag:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Add this line -->
    <uses-permission android:name="android.permission.INTERNET"/>

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.YourApp"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.YourApp">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

### Step 5: Update activity_main.xml

Replace the entire contents of `app/src/main/res/layout/activity_main.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main_nav_host"
    android:name="dev.hotwire.navigation.navigator.NavigatorHost"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="false" />
```

---

### Step 6: Update MainActivity.kt

Replace the entire contents of `app/src/main/java/com/example/yourapp/MainActivity.kt`:

```kotlin
package com.example.yourapp  // Update to match your package name

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import dev.hotwire.navigation.activities.HotwireActivity
import dev.hotwire.navigation.navigator.NavigatorConfiguration
import dev.hotwire.navigation.util.applyDefaultImeWindowInsets

class MainActivity : HotwireActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.main_nav_host).applyDefaultImeWindowInsets()
    }

    override fun navigatorConfigurations() = listOf(
        NavigatorConfiguration(
            name = "main",
            startLocation = "http://10.0.2.2:3000",  // See networking section below
            navigatorHostId = R.id.main_nav_host
        )
    )
}
```

**Important URL Configuration:**

| Environment | URL to Use | Notes |
|-------------|------------|-------|
| Local development (emulator) | `http://10.0.2.2:3000` | Special IP for emulator to access host machine |
| Local development (physical device) | `http://192.168.1.X:3000` | Use your computer's local IP |
| Production | `https://yourdomain.com` | Your production URL |
| Testing/Demo | `https://hotwire-native-demo.dev` | Hotwire's demo server |

---

### Step 7: Sync Gradle Files

1. Click **"Sync Now"** in the notification bar
2. Or **File** → **Sync Project with Gradle Files**
3. Wait for sync to complete (may take a few minutes)

---

## Common Issues & Solutions

### Issue 1: "Dependency requires compileSdk 35"

**Error:**
```
Dependency 'androidx.core:core-ktx:1.16.0' requires libraries and applications that
depend on it to compile against version 35 or later of the Android APIs.
:app is currently compiled against android-34.
```

**Solution:**
Update `compileSdk` and `targetSdk` to `35` in `app/build.gradle.kts`:
```kotlin
android {
    compileSdk = 35
    defaultConfig {
        targetSdk = 35
    }
}
```

---

### Issue 2: "Compiled with incompatible Kotlin version"

**Error:**
```
Class 'dev.hotwire.navigation.activities.HotwireActivity' was compiled with an 
incompatible version of Kotlin. The actual metadata version is 2.1.0, but the 
compiler version 1.9.0 can read versions up to 2.0.0.
```

**Solution:**
Update Kotlin version in `gradle/libs.versions.toml`:
```toml
[versions]
kotlin = "2.1.0"  # Change from 1.9.24 to 2.1.0
```

Then sync Gradle files.

---

### Issue 3: Emulator Window Not Showing on Mac

**Problem:** Emulator is running (green dot in Device Manager) but window doesn't appear.

**Solution 1: Launch with Software Graphics (RECOMMENDED)**

1. Stop any running emulators in Device Manager
2. Open Terminal
3. Navigate to emulator directory and launch:

```bash
cd ~/Library/Android/sdk/emulator
./emulator -list-avds  # List available emulators
./emulator -avd "YOUR_AVD_NAME" -gpu swiftshader_indirect
```

**Solution 2: Change Graphics Settings**

1. Stop the emulator
2. In Device Manager, click **⋮** → **Edit**
3. Change **Graphics** from "Automatic" to **"Hardware - GLES 2.0"**
4. Click **"Show Advanced Settings"**
5. Set **Boot option** to **"Cold boot"**
6. Click **Finish**

**Solution 3: Create New Emulator**

1. **Tools** → **Device Manager**
2. Click **+** (Create Device)
3. Select **Pixel 7** or **Pixel 6**
4. Choose **API 35** system image
5. On configuration screen:
   - Set **Graphics**: **Hardware - GLES 2.0**
   - Click **Show Advanced Settings**
   - Set **Boot option**: **Cold boot**
   - Set **RAM**: 2048 MB or higher
6. Click **Finish**

---

### Issue 4: "Unknown Error loading Page" in Emulator

**Problem:** App launches but shows "Unknown Error loading Page"

**Causes & Solutions:**

1. **Wrong URL for emulator:**
   - Use `http://10.0.2.2:3000` instead of `localhost:3000` or `192.168.1.X:3000`
   - The emulator uses `10.0.2.2` as an alias for your host machine's `127.0.0.1`

### Issue 5: Build Fails with Gradle Errors

**Solution 1: Clean and Rebuild**
```bash
./gradlew clean
./gradlew build
```

Or in Android Studio:
- **Build** → **Clean Project**
- **Build** → **Rebuild Project**

**Solution 2: Invalidate Caches**
- **File** → **Invalidate Caches...**
- Select **Invalidate and Restart**

**Solution 3: Delete Build Folders**
```bash
rm -rf .gradle
rm -rf build
rm -rf app/build
./gradlew clean
```

---

## 🚀 Running the App

### On Emulator

1. **Create/Start Emulator:**
   - **Tools** → **Device Manager**
   - Click **▶** next to your emulator
   - Wait for boot (1-2 minutes)

2. **Run App:**
   - Click green **Run** button (▶) in toolbar
   - Or **Run** → **Run 'app'**
   - Select your emulator from device list

3. **If emulator doesn't appear, use Terminal:**
   ```bash
   cd ~/Library/Android/sdk/emulator
   ./emulator -avd "YOUR_AVD_NAME" -gpu swiftshader_indirect
   ```

## 📝 Important Notes

### Network Configuration

| Scenario | URL Format | Example |
|----------|------------|---------|
| Emulator + Local Server | `http://10.0.2.2:PORT` | `http://10.0.2.2:3000` |
| Physical Device + Local Server | `http://YOUR_LOCAL_IP:PORT` | `http://192.168.1.15:3000` |
| Production | `https://yourdomain.com` | `https://myapp.com` |


### Version Compatibility Matrix

| Component | Minimum Version | Recommended |
|-----------|----------------|-------------|
| Android Studio | Hedgehog (2023.1.1) | Latest Stable |
| Kotlin | 2.0.0 | 2.1.0+ |
| compileSdk | 34 | 35 |
| targetSdk | 28 | 35 |
| minSdk | 28 | 28 |
| Hotwire Native | 1.0.0 | 1.2.4+ |

---

### Useful Commands

**Gradle:**
```bash
./gradlew clean              # Clean build files
./gradlew build              # Build project
./gradlew assembleDebug      # Build debug APK
./gradlew installDebug       # Install on connected device
```

**ADB (Android Debug Bridge):**
```bash
adb devices                  # List connected devices
adb kill-server             # Restart ADB
adb start-server
adb logcat                  # View device logs
adb -s DEVICE_ID shell      # Shell into specific device
```

**Emulator:**
```bash
cd ~/Library/Android/sdk/emulator
./emulator -list-avds                                    # List emulators
./emulator -avd "NAME"                                   # Start emulator
./emulator -avd "NAME" -gpu swiftshader_indirect        # Force software graphics
./emulator -avd "NAME" -no-snapshot-load                # Cold boot
```

---

### Troubleshooting Checklist

When something goes wrong, check these in order:

- [ ] Kotlin version is 2.1.0 or higher (`gradle/libs.versions.toml`)
- [ ] compileSdk is 35 or higher (`app/build.gradle.kts`)
- [ ] Internet permission added to `AndroidManifest.xml`
- [ ] Hotwire dependencies added to `app/build.gradle.kts`
- [ ] Gradle files synced successfully
- [ ] Build completes without errors
- [ ] Server is running and accessible
- [ ] Using correct URL for environment (10.0.2.2 for emulator)
- [ ] Emulator/device is connected and showing in Device Manager

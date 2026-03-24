# Konnekt
A real-time chat application for Android focused on reliable messaging and a clean user experience.

![Android](https://img.shields.io/badge/Android-68c06e?logo=android&style=for-the-badge&logoColor=white) 
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-8b48fc?logo=kotlin&style=for-the-badge&logoColor=white) 
![Jetpack Compose Multiplatform](https://img.shields.io/badge/Jetpack%20Compose-4285f4?logo=jetpack-compose&style=for-the-badge&logoColor=white) 
![Supabase](https://img.shields.io/badge/Supabase-36454f?logo=supabase&style=for-the-badge&logoColor=3fcf8e)
![Firebase](https://img.shields.io/badge/Firebase-dd2c00?logo=firebase&style=for-the-badge&logoColor=ffbf00)<br/>

## ✨ Features

💬 **Real-Time Messaging** – Send and receive messages instantly with live updates.

✉️ **Flexible Messaging** – Send messages using text, images, or voice recordings.

👥 **Multiple Chat Types** – Supports personal chats, group chats, and chat rooms.

🟢 **Online Status** – See when users are online or offline in real time.

🔔 **Push Notifications** – Get notified about new messages and reply directly from notifications.

📱 **Modern UI with Jetpack Compose** – Clean, responsive, and easy to navigate interface.

## 📥 Installation
### 🔹 Requirements
- Android Studio.
- Android device running **Android 8.0 (Oreo) or higher**.

### 🔥 Firebase Cloud Messaging
1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app to the Firebase project and set the package name to `nrr.konnekt.debug` (debug) or `nrr.konnekt` (release).
3. Download the `google-services.json` file and place it under `app/` directory.
4. Enable Cloud Messaging in the Firebase Console.

### ☁️ Server
Read the [Supabase setup](/core/network/supabase/README.md) guide to configure the default Supabase server, or use a custom server by implementing classes in the [core/domain](/core/domain) module within a new module under [core/network](/core/network) module, then add it as a dependency in the [core/data](/core/data) Gradle build file.
```kotlin
dependencies {
    implementation(projects.konnekt.core.network.<your module name>)
}
```
Remove the default Supabase implementation:
```kotlin
implementation(projects.konnekt.core.network.supabase)
```

### 🔹 Debug Build
1. Clone the repository

    ```bash
    git clone https://github.com/kite1412/android-konnekt.git
    ```
2. Open the downloaded repository in Android Studio.
3. Simply click run (▶️) button at the top of the Android Studio to install and launch the app.

### 🔹 Release Build (preferred for smaller apk size)
1. Clone the repository

    ```bash
    git clone https://github.com/kite1412/android-konnekt.git
    ```
2. Open the downloaded repository in Android Studio.
3. Navigate to **Build > Generate Signed APP Bundle / APK**.
4. Select **APK** to build signed APK.
5. Choose an existing `.jks` file or create a new one to sign the APK.
6. Select **release** as Build Variants then **Create**, wait for the APK file generation to complete.
7. In project's root directory, run  following command:

    ```bash
    adb install app/release/app-release.apk
    ```
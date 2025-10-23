plugins {
    alias(libs.plugins.konnekt.android.feature)
}

android {
    namespace = "nrr.konnekt.feature.chats"
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

dependencies {
    implementation(projects.core.network.upload.domain)
    implementation(projects.core.network.upload.util)
}
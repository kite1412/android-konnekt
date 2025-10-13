plugins {
    alias(libs.plugins.konnekt.android.feature)
}

android {
    namespace = "nrr.konnekt.feature.conversation"
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

dependencies {
    implementation(projects.core.player)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
}
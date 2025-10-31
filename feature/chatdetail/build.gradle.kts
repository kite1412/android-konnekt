plugins {
    alias(libs.plugins.konnekt.android.feature)
}

android {
    namespace = "nrr.konnekt.feature.chatdetail"
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}
plugins {
    alias(libs.plugins.konnekt.android.library.compose)
}

android {
    namespace = "nrr.konnekt.core.ui"

    kotlin {
        sourceSets.all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.domain)
    implementation(projects.core.player)
}
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
    implementation(projects.core.storage.file)
    implementation(projects.core.network.upload.domain)
    implementation(projects.core.network.upload.util)

    implementation(libs.androidx.core.ktx)
}
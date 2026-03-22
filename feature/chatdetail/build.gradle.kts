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

dependencies {
    implementation(projects.konnekt.core.network.upload.util)
    implementation(projects.konnekt.core.storage.datastore)
}
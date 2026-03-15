plugins {
    alias(libs.plugins.konnekt.android.feature)
}

android {
    namespace = "nrr.konnekt.feature.profile"
}

dependencies {
    implementation(projects.core.network.upload.util)
    implementation(projects.core.network.upload.domain)
}
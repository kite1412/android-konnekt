plugins {
    alias(libs.plugins.konnekt.android.library)
    alias(libs.plugins.konnekt.hilt)
}

android {
    namespace = "nrr.konnekt.core.notification"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.network.upload.domain)
    implementation(projects.core.network.upload.util)

    implementation(libs.androidx.core.ktx)
}
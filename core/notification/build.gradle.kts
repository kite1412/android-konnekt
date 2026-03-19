plugins {
    alias(libs.plugins.konnekt.android.library)
    alias(libs.plugins.konnekt.hilt)
}

android {
    namespace = "nrr.konnekt.core.notification"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.core.ktx)
}
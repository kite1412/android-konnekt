plugins {
    alias(libs.plugins.konnekt.android.library)
    alias(libs.plugins.konnekt.hilt)
}

android {
    namespace = "nrr.konnekt.core.network.upload.util"
}

dependencies {
    implementation(projects.core.network.upload.domain)
    implementation(projects.core.storage.file)
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines.core)
}
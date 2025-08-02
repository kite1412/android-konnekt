plugins {
    alias(libs.plugins.konnekt.android.library)
    alias(libs.plugins.konnekt.hilt)
}

android {
    namespace = "nrr.konnekt.core.data"
}

dependencies {
    api(projects.konnekt.core.network.api)

    implementation(projects.konnekt.core.network.supabase)
}
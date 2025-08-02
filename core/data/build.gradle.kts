plugins {
    alias(libs.plugins.konnekt.android.library)
    alias(libs.plugins.konnekt.hilt)
}

android {
    namespace = "nrr.konnekt.core.data"
}

dependencies {
    implementation(projects.konnekt.core.domain)

    implementation(projects.konnekt.core.network.supabase)
}
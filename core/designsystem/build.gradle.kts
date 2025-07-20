plugins {
    alias(libs.plugins.konnekt.android.library.compose)
}

android {
    namespace = "nrr.konnekt.designsystem"
}

dependencies {
    api(libs.androidx.material3) // includes androidx.compose.ui.*
}
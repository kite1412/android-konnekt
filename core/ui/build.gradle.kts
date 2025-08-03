plugins {
    alias(libs.plugins.konnekt.android.library.compose)
}

android {
    namespace = "nrr.konnekt.core.ui"
}

dependencies {
    api(projects.core.designsystem)
}
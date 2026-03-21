plugins {
    alias(libs.plugins.konnekt.android.library)
}

android {
    namespace = "nrr.konnekt.core.storage.datastore"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
}
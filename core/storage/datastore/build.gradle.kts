plugins {
    alias(libs.plugins.konnekt.android.library)
}

android {
    namespace = "nrr.konnekt.core.storage.datastore"
}

dependencies {
    api(libs.androidx.datastore.preferences)
}
plugins {
    alias(libs.plugins.konnekt.jvm.library)
    alias(libs.plugins.konnekt.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
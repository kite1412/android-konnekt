plugins {
    alias(libs.plugins.konnekt.android.library)
}

android {
    namespace = "nrr.konnekt.core.player"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-XXLanguage:+WhenGuards")
    }
}

dependencies {
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.lifecycle.common.jvm)
}
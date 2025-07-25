plugins {
    alias(libs.plugins.konnekt.jvm.library)
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

dependencies {
    api(libs.kotlinx.datetime)
}
plugins {
    alias(libs.plugins.konnekt.jvm.library)
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

dependencies {
    api(libs.kotlinx.datetime)

    implementation(libs.kotlinx.serialization.json)
}
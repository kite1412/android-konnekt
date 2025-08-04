plugins {
    alias(libs.plugins.konnekt.jvm.library)
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

dependencies {
    api(projects.konnekt.core.model)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
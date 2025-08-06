import java.util.Properties

plugins {
    alias(libs.plugins.konnekt.android.library)
    alias(libs.plugins.konnekt.hilt)
    kotlin("plugin.serialization")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}
val supabaseUrl = localProperties.getProperty("SUPABASE_URL")
    ?: throw GradleException("SUPABASE_URL not found in local.properties")
val supabaseKey = localProperties.getProperty("SUPABASE_KEY")
    ?: throw GradleException("SUPABASE_KEY not found in local.properties")

private fun String.quote() = "\"$this\""

android {
    namespace = "nrr.konnekt.core.network.supabase"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "SUPABASE_URL", supabaseUrl.quote())
        buildConfigField("String", "SUPABASE_KEY", supabaseKey.quote())
    }

    kotlin {
        sourceSets.all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
}

dependencies {
    implementation(projects.konnekt.core.domain)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.db)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.kotlinx.serialization.json)

    androidTestImplementation(libs.kotlinx.coroutines.test)
}
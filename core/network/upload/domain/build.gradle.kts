plugins {
    alias(libs.plugins.konnekt.jvm.library)
}
dependencies {
    implementation(projects.konnekt.core.model)

    implementation(libs.kotlinx.coroutines.core)
}

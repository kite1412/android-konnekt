plugins {
    alias(libs.plugins.konnekt.jvm.library)
}

dependencies {
    api(projects.konnekt.core.model)

    implementation(libs.javax.inject)
}
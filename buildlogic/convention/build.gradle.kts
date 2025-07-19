import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gp)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gp)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "konnekt.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "konnekt.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
    }
}
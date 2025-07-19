package nrr.konnekt

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val composeBom = platform(libs.findLibrary("androidx.compose.bom").get())
            "implementation"(composeBom)
            "androidTestImplementation"(composeBom)
            "debugImplementation"(libs.findLibrary("androidx.ui.tooling.preview").get())
        }
    }
}
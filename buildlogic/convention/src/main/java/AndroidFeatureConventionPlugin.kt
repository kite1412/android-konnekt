import nrr.konnekt.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("konnekt.android.library.compose")
                apply("konnekt.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                // TODO later change with higher level module that includes all components
                "implementation"(project(":core:designsystem"))

                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }
}
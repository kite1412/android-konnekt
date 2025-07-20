import com.android.build.gradle.LibraryExtension
import nrr.konnekt.ExtensionType
import nrr.konnekt.configureBuildTypes
import nrr.konnekt.configureKotlinAndroid
import nrr.konnekt.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig.targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                configureKotlinAndroid(this)
                configureBuildTypes(this, ExtensionType.LIBRARY)

                defaultConfig {
                    // addition for debug build type
                    proguardFile("proguard-rules.pro")
                    consumerProguardFiles("consumer-rules.pro")
                }

                dependencies {
                    "testImplementation"(libs.findLibrary("junit").get())
                    "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
                    "androidTestImplementation"(libs.findLibrary("androidx.espresso.core").get())
                }
            }
        }
    }
}
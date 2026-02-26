package nrr.konnekt

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension,
    extensionType: ExtensionType
) {
    when (extensionType) {
        ExtensionType.APPLICATION -> {
            (commonExtension as ApplicationExtension).apply {
                buildTypes {
                    release {
                        configureReleaseBuildType(extensionType, commonExtension)
                    }
                    debug {
                        applicationIdSuffix = ".debug"
                        versionNameSuffix = "-debug"
                    }
                }
            }
        }
        ExtensionType.LIBRARY -> {
            (commonExtension as LibraryExtension).apply {
                buildTypes {
                    release {
                        configureReleaseBuildType(extensionType, commonExtension)
                    }
                }
            }
        }
    }
}

private fun BuildType.configureReleaseBuildType(
    extensionType: ExtensionType,
    commonExtension: CommonExtension
) {
    isMinifyEnabled = extensionType == ExtensionType.APPLICATION
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
import com.android.build.api.dsl.ApplicationExtension
import nrr.konnekt.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("konnekt.android.application")
            }

            configureAndroidCompose(extensions.getByType<ApplicationExtension>())
        }
    }
}
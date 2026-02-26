
import com.android.build.api.dsl.LibraryExtension
import nrr.konnekt.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("konnekt.android.library")
            }

            val libExtension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(libExtension)
        }
    }
}
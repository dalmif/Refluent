import com.android.build.gradle.LibraryExtension
import io.kayt.configureAndroidCompose
import io.kayt.extensions.alias
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.android.library)
            pluginManager.alias(libs.plugins.kotlin.compose)
            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }

}
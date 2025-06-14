import com.android.build.api.dsl.ApplicationExtension
import io.kayt.configureAndroidCompose
import io.kayt.extensions.alias
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.android.application)
                alias(libs.plugins.kotlin.compose)
            }

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)

            dependencies {
                implementation(project(":core:ui"))
            }
        }
    }
}
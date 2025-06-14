import io.kayt.configureKotlinJvm
import io.kayt.extensions.alias
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.kotlin.jvm)
                alias(libs.plugins.dalmif.android.lint)
                alias(libs.plugins.dalmif.detekt)
            }
            configureKotlinJvm()
        }
    }
}

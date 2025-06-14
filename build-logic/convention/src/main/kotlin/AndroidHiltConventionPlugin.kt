import io.kayt.extensions.alias
import io.kayt.extensions.implementation
import io.kayt.extensions.ksp
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.hilt)
                alias(libs.plugins.ksp)
            }

            dependencies {
                implementation(libs.hilt.android)
                ksp(libs.hilt.compiler)
            }
        }
    }
}
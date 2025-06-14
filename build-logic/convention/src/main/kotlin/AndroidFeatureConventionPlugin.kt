import io.kayt.extensions.alias
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                alias(libs.plugins.dalmif.android.library)
                alias(libs.plugins.dalmif.android.library.compose)
                alias(libs.plugins.dalmif.android.hilt)
                alias(libs.plugins.dalmif.kotlin.serialization)
            }

            dependencies {
                implementation(project(":core:ui"))
                implementation(project(":core:model"))
                implementation(project(":core:domain"))
            }
        }
    }
}
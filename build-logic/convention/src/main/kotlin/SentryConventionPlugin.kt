import io.kayt.extensions.alias
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SentryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.sentry)

            dependencies {
                implementation(libs.sentry)
                implementation(libs.sentry.compose)
            }
        }
    }
}
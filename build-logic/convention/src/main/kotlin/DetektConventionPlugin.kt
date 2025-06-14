import io.kayt.extensions.alias
import io.kayt.extensions.libs
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.detekt)

            dependencies {
                "detektPlugins"(libs.detekt.formatting)
            }

            tasks.withType<Detekt>().configureEach {
                config.setFrom(rootProject.file("config/detekt.yml"))
                buildUponDefaultConfig = true
                autoCorrect = true
                basePath = rootProject.rootDir.absolutePath
                reports {
                    xml.required = false
                    sarif.required = false
                    html.required = false
                    md.required = false
                }
            }
        }
    }
}
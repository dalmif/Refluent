import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidMedia3ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                implementation(libs.androidx.media3.exoplayer)
                implementation(libs.androidx.media3.session)
            }
        }
    }
}
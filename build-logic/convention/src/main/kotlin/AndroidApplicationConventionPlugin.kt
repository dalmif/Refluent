import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import io.kayt.configureCommonAndroidx
import io.kayt.configureKotlinAndroid
import io.kayt.configurePrintApksTask
import io.kayt.extensions.alias
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.android.application)
                alias(libs.plugins.kotlin.android)
                alias(libs.plugins.dalmif.android.hilt)
                alias(libs.plugins.dalmif.android.lint)
                alias(libs.plugins.dalmif.detekt)
                alias(libs.plugins.dalmif.kotlin.serialization)
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureCommonAndroidx(this)
                defaultConfig.targetSdk = libs.versions.targetSdkVersion.get().toInt()
                buildFeatures.buildConfig = true
            }
            extensions.configure<ApplicationAndroidComponentsExtension> {
                configurePrintApksTask(this)
            }

            dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:domain"))
            }
        }
    }

}
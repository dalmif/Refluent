import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import io.kayt.configureCommonAndroidx
import io.kayt.configureKotlinAndroid
import io.kayt.configurePrintApksTask
import io.kayt.disableUnnecessaryAndroidTests
import io.kayt.extensions.alias
import io.kayt.extensions.androidTestImplementation
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import io.kayt.extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.android.library)
                alias(libs.plugins.kotlin.android)
                alias(libs.plugins.dalmif.android.lint)
                alias(libs.plugins.dalmif.detekt)
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureCommonAndroidx(this)
                defaultConfig.targetSdk = libs.versions.targetSdkVersion.get().toInt()
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configurePrintApksTask(this)
                disableUnnecessaryAndroidTests(target)
            }
            dependencies {
                androidTestImplementation(kotlin("test"))
                testImplementation(kotlin("test"))
            }
        }
    }
}
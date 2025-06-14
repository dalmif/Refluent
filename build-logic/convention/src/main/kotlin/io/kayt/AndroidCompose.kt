package io.kayt

import com.android.build.api.dsl.CommonExtension
import io.kayt.extensions.debugImplementation
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import io.kayt.extensions.lintChecks
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs.androidx.compose.bom
            implementation(platform(bom))
            implementation(libs.kotlin.immutable)

            implementation(libs.androidx.lifecycle.compose)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)
            debugImplementation(libs.androidx.compose.ui.tooling)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.hilt.navigation.compose)
            implementation(libs.androidx.compose.foundation)
            implementation(libs.coil.compose)
            implementation(libs.coil.svg)
            implementation(libs.lottie.compose)
            implementation(libs.androidx.window)

            implementation(libs.androidx.compose.ui.tooling.preview)
            debugImplementation(libs.androidx.compose.ui.tooling)

            lintChecks(libs.lint.compose)
        }

    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = flatMap {
            rootProject.layout.buildDirectory.dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        stabilityConfigurationFile = rootProject.layout.projectDirectory.file("compose_compiler_config.conf")

        enableStrongSkippingMode = true
    }
}
package io.kayt

import com.android.build.api.dsl.CommonExtension
import io.kayt.extensions.implementation
import io.kayt.extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCommonAndroidx(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        dependencies {
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.core)
        }
    }
}
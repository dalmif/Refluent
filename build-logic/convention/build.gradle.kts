import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}


dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.grgit.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    gradle.serviceOf<DependenciesAccessors>().classes.asFiles.forEach {
        compileOnly(files(it.absolutePath))
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dalmif.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidApplicationCompose") {
            id = "dalmif.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("androidLibrary") {
            id = "dalmif.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidLibraryCompose") {
            id = "dalmif.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        register("androidLint") {
            id = "dalmif.android.lint"
            implementationClass = "AndroidLintConventionPlugin"
        }

        register("androidHilt") {
            id = "dalmif.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("featurePlugin") {
            id = "dalmif.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("jvmLibrary") {
            id = "dalmif.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }

        register("media3") {
            id = "dalmif.android.media"
            implementationClass = "AndroidMedia3ConventionPlugin"
        }

        register("gitVersioning") {
            id = "dalmif.git.versioning"
            implementationClass = "GitVersioningConventionPlugin"
        }

        register("detekt"){
            id = "dalmif.detekt"
            implementationClass = "DetektConventionPlugin"
        }

        register("sentry"){
            id = "dalmif.sentry"
            implementationClass = "SentryConventionPlugin"
        }

        register("kotlinSerialization") {
            id = "dalmif.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }

        register("secrets") {
            id = "dalmif.secrets"
            implementationClass = "SecretsConventionPlugin"
        }
    }
}
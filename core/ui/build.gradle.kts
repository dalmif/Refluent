plugins {
    alias(libs.plugins.dalmif.android.library)
    alias(libs.plugins.dalmif.android.library.compose)
}

android {
    namespace = "io.kayt.refluent.core.ui"
    buildTypes {
        create("beta")
    }

    defaultConfig {
        buildConfigField("String", "RELEASE_BUILD_TYPE_NAME", "\"release\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))
}
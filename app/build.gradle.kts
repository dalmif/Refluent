plugins {
    alias(libs.plugins.dalmif.android.application)
    alias(libs.plugins.dalmif.android.application.compose)
    alias(libs.plugins.dalmif.git.versioning)
    alias(libs.plugins.dalmif.android.hilt)
    alias(libs.plugins.dalmif.secrets)
}

android {
    namespace = "io.kayt.refluent"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.kayt.refluent"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
//    implementation(project(":feature:home"))
}
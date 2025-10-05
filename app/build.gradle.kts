plugins {
    alias(libs.plugins.dalmif.android.application)
    alias(libs.plugins.dalmif.android.application.compose)
    alias(libs.plugins.dalmif.git.versioning)
    alias(libs.plugins.dalmif.android.hilt)
    alias(libs.plugins.dalmif.secrets)
    alias(libs.plugins.google.service.plugin)
    alias(libs.plugins.google.crashlytics.plugin)
}

android {
    namespace = "io.kayt.refluent"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.refluent"
        minSdk = 28
        targetSdk = 36
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

    //Firebase and crashlytics
    implementation(libs.firebase.bom)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.analytics)

    implementation(project(":feature:home"))
    implementation(project(":feature:deck"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))
}
import java.io.FileInputStream
import java.util.Properties

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
        versionCode = gitVersion.generateVersionCode()
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("internal") {
            val keystorePropertiesFile = rootProject.file("secrets/signing/internalsign.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = rootProject.file("secrets/signing/internal.jks")
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        val internalSigning = signingConfigs.getByName("internal")
        val appNameSuffixKey = "appNameSuffix"
        release {
            signingConfig = internalSigning
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders[appNameSuffixKey] = ""
        }
        debug {
            signingConfig = internalSigning
            versionNameSuffix = "-${gitVersion.generateVersionName()}"
            applicationIdSuffix = ".beta"
            manifestPlaceholders[appNameSuffixKey] = " beta"
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.analytics)

    implementation(libs.androidx.splash)

    implementation(project(":feature:home"))
    implementation(project(":feature:deck"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))
}
import java.util.Properties

plugins {
    alias(libs.plugins.dalmif.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dalmif.android.hilt)
}

android {
    namespace = "io.kayt.refluent.core.data"
    defaultConfig {
        val properties = Properties()
        val secretsFile = project.rootProject.file("secrets/secrets.properties")
        runCatching {
            properties.load(secretsFile.inputStream())
        }
        val geminiKey = properties.getProperty("GEMINI_API_KEY") ?: throw IllegalStateException()
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(libs.generativeai)
    implementation(libs.kotlin.serialization.json)

    implementation("io.socket:socket.io-client:2.0.0") {
        exclude(group = "org.json", module = "json")
    }
}
plugins {
    alias(libs.plugins.dalmif.android.library)
    alias(libs.plugins.dalmif.android.hilt)
}

android {
    namespace = "io.kayt.refluent.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
}
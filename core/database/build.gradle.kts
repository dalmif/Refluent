plugins {
    alias(libs.plugins.dalmif.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.kayt.refluent.core.ui"
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
}
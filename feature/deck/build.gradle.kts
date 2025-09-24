plugins {
    alias(libs.plugins.dalmif.android.feature)
}

android {
    namespace = "io.kayt.feature.content"
}

dependencies {
    implementation(libs.richeditor.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(project(":core:data"))
    implementation(libs.compose.swipeable.cards)
}
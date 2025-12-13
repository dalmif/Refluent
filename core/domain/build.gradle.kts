plugins {
    alias(libs.plugins.dalmif.jvm.library)
}
dependencies {
    implementation(project(":core:model"))
}

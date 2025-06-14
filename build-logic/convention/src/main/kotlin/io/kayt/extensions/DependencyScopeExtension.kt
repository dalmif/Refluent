package io.kayt.extensions

import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

fun DependencyHandlerScope.compileOnly(dependencyNotation: Any) {
    add("compileOnly", dependencyNotation)
}

fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any) {
    add("debugImplementation", dependencyNotation)
}

fun DependencyHandlerScope.coreLibraryDesugaring(dependencyNotation: Any) {
    add("coreLibraryDesugaring", dependencyNotation)
}

fun DependencyHandlerScope.ksp(dependencyNotation: Any) {
    add("ksp", dependencyNotation)
}

fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: Any) {
    add("androidTestImplementation", dependencyNotation)
}

fun DependencyHandlerScope.testImplementation(dependencyNotation: Any) {
    add("testImplementation", dependencyNotation)
}

fun DependencyHandlerScope.lintChecks(dependencyNotation: Any) {
    add("lintChecks", dependencyNotation)
}

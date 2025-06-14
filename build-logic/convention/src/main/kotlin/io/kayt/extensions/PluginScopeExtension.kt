package io.kayt.extensions

import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.plugin.use.PluginDependency

fun PluginManager.alias(notation: Provider<PluginDependency>) {
    apply(notation.get().pluginId)
}
fun PluginManager.alias(notation: ProviderConvertible<PluginDependency>) {
    apply(notation.asProvider().get().pluginId)
}
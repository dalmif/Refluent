import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File
import java.util.Properties

class SecretsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val secretsFile = project.rootProject.file("config/secrets/secrets.properties")
        project.extensions.create("secrets", SecretsContainerExtension::class.java, secretsFile)
    }
}

open class SecretsContainerExtension(
    private val secretsFile: File,
) {
    private val secrets: Properties by lazy(LazyThreadSafetyMode.NONE) {
        val properties = Properties()
        runCatching {
            properties.load(secretsFile.inputStream())
        }.onFailure {
            throw IllegalStateException(
                "Could not load secrets file: ${secretsFile.absolutePath}. " +
                    "Make sure to load secrets with `pull-secrets` sh script", it
            )
        }
        properties
    }

    operator fun get(key: String): String {
        return secrets.getProperty(key) ?: error(
            "Secret with key $key is missing. " +
                "Make sure Make sure to load secrets with `pull-secrets` sh script"
        )
    }
}
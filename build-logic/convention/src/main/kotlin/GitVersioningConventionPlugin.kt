import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.time.format.DateTimeFormatter

class GitVersioningConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            extensions.create("gitVersion", GitVersioning::class.java, project)
        }
    }

    open class GitVersioning(project: Project) {
        private val git = Grgit.open(mapOf("currentDir" to project.rootDir))

        private fun getLatestCommit(): Commit {
            return git.log(mapOf("includes" to arrayOf("HEAD"), "maxCommits" to 1)).first()
        }

        private fun getCommitsOfCurrentBranch(): List<Commit> {
            return git.log(mapOf("includes" to arrayOf("HEAD")))
        }

        fun generateVersionName(): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss")
            val lastCommit = getLatestCommit()
            val lastCommitDateTime = lastCommit.dateTime
            val formattedUtcTime = lastCommitDateTime.format(formatter)
            val hash = lastCommit.abbreviatedId
            return "$formattedUtcTime.$hash"
        }

        fun generateVersionCode(): Int {
            // the number of commits on the current branch as build number
            return getCommitsOfCurrentBranch().size
        }
    }
}

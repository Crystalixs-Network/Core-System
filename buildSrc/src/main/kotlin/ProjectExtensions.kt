import org.gradle.api.Project
import java.util.*

const val paperPort = "30066"

fun Project.mavenArtifact(): String {
    val rawName = property("plugin-name") as String
    return rawName.trim()
        .lowercase()
        .replace(Regex("[^a-z0-9\\-.]"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
}

fun Project.minecraftPluginMainClass(): String {
    val rawName = property("plugin-name") as String
    val name = rawName
        .split(Regex("[\\s_-]+"))
        .filter { it.isNotBlank() }
        .joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } }
        .replace(Regex("[^A-Za-z0-9]"), "")
        .replace(Regex("^[0-9]+"), "")

    return "${name}Plugin"
}

fun Project.pluginAuthors(defaultAuthors: List<String> = listOf("Unknown")): List<String> {
    val rawList = property("authors") as? String? ?: return defaultAuthors

    return rawList
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() && it.isNotBlank() }
}

fun Project.configurePaperServer() {
    val serverDir = layout.dir(provider { file("run") }).get().asFile
    serverDir.mkdirs()

    // Auto accept the eula
    val eulaFile = serverDir.resolve("eula.txt")
    eulaFile.writeText("eula=true")

    // Set port of the paper backend server to 30066 and disable online mode
    val propertiesFile = serverDir.resolve("server.properties")
    val properties = Properties()
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
    }

    properties.setProperty("server-port", paperPort)
    properties.setProperty("online-mode", "false")
    properties.store(propertiesFile.outputStream(), null)

    // Enable velocity
    val configDir = serverDir.resolve("config")
    configDir.mkdirs()

    val secret = DevEnvironment.readForwardingSecret(rootDir)
    configDir.resolve("paper-global.yml").writeText(
        """
             proxies:
              velocity:
                enabled: true
                online-mode: true
                secret: "$secret"
        """.trimIndent(), Charsets.UTF_8
    )
}

fun Project.configureVelocityProxy() {
    val secretFile = DevEnvironment.ensureForwardingSecretFile(rootDir)

    val serverDir = layout.dir(provider { file("run") }).get().asFile
    if (serverDir.exists()) serverDir.deleteRecursively()
    serverDir.mkdirs()

    val toml = serverDir.resolve("velocity.toml")
    if (toml.exists()) toml.delete()

    toml.writeText(
        """
            config-version = "2.7"
            bind = "0.0.0.0:25565"
            online-mode = true
            player-info-forwarding-mode = "modern"
            forwarding-secret-file = "${secretFile.absolutePath.replace("\\", "/")}"
            
            [servers]
            lobby = "127.0.0.1:$paperPort"

            try = [
              "lobby"
            ]
            
            [forced-hosts]
            lobby = "lobby"
        """.trimIndent(), Charsets.UTF_8
    )
}
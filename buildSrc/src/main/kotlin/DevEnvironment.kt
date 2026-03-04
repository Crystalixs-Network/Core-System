import java.io.File

object DevEnvironment {

    fun ensureForwardingSecretFile(rootDir: File): File {
        val secretsDirectory = rootDir.resolve("secrets")
        secretsDirectory.mkdirs()

        val secretFile = secretsDirectory.resolve("forwarding.secret")
        if (!secretFile.exists()) {
            val secret = List(32) {
                ('A'..'Z') + ('a'..'z') + ('0'..'9')
            }.flatten().let { chars -> (1..32).map { chars.random() } }.joinToString("")

            secretFile.writeText(secret)
        }
        return secretFile
    }

    fun readForwardingSecret(rootDir: File): String {
        return ensureForwardingSecretFile(rootDir).readText().trim()
    }

}
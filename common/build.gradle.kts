plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

tasks {
    val artifact = project.mavenArtifact()

    shadowJar {
        // Dieses Mapping sorgt dafür, dass die Klassen des Dependencies-Pakets
        // in einen eigenen Namespace verschoben werden, wenn der Shadow-JAR gebaut wird.
        // So vermeiden wir Konflikte mit anderen Libraries, die dieselben Klassen enthalten.
        // Format: originalPackage → relocatedPackage
        // Beispiel: io.github.foo → foo

        // Entferne die nachfolgende Kommentierung, sobald eine Library in das Plugin fest zur Laufzeit integriert werden muss.

        /*
        val mapping = mapOf("" to "")

        val base = "$group.$artifact.common.libs"
        for ((pattern, name) in mapping) relocate(pattern, "$base.$name")
         */
    }

    jar {
        archiveBaseName.set("$artifact-${rootProject.version}-common")
    }

    build {
        dependsOn(shadowJar)
    }
}
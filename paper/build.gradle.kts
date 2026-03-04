plugins {
    alias(libs.plugins.bukkitConvention)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.brigadier)

    implementation(project(":common"))
    implementation(libs.bundles.cloudPaper)
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

        val mapping = mapOf(
            libs.cloud.paper to "cloud"
        )

        val base = "$group.$artifact.paper.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    jar {
        archiveBaseName.set("$artifact-paper")
    }

    bukkitPluginYaml {
        val mainClass = project.minecraftPluginMainClass()

        main = "$group.$artifact.paper.$mainClass"
        name = rootProject.property("plugin-name") as String
        authors = project.pluginAuthors()
        apiVersion = "1.21"
    }

    runServer {
        minecraftVersion("1.21.11")
        doFirst {
            configurePaperServer()
        }
    }
}
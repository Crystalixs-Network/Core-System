import xyz.jpenilla.runpaper.task.RunServer

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
    implementation(libs.configurate.hocon)
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
            libs.cloud.paper to "cloud",
            libs.configurate.hocon to "configurate",
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


    registerBackendServer("runLobby", "run-lobby", DevEnvironment.LOBBY_PORT)
    registerBackendServer("runGame", "run-game", DevEnvironment.GAME_PORT)
}

fun registerBackendServer(name: String, runDirName: String, port: String) {
    val copyTask = tasks.register<CopyPlugin>("copy${name}Plugin") {
        runDir.set(runDirName)

        dependsOn("shadowJar")
        from(tasks.named("shadowJar"))
        into(layout.dir(provider { file("${runDir.get()}/plugins") }))
        doFirst {
            println("Copying plugin into ${runDir.get()}/plugins")
        }
    }

    tasks.register<RunServer>(name) {
        minecraftVersion("1.21.11")
        runDirectory = file(runDirName)
        dependsOn(copyTask)
        doFirst {
            configurePaperServer(runDirName, port)
        }
    }
}
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
    implementation(project(":persistence"))
    implementation(libs.bundles.cloudPaper)
    implementation(libs.configurate.hocon)
    implementation(libs.invui)
    implementation(libs.lettuce)
}

tasks {
    val artifact = project.mavenArtifact()

    shadowJar {
        val mapping = mapOf(
            libs.cloud.paper to "cloud",
            libs.configurate.hocon to "configurate.hocon",
            libs.invui to "invui",
            libs.lettuce to "lettuce",
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
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    alias(libs.plugins.bukkitConvention)
    alias(libs.plugins.paperConvention)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.brigadier)
    compileOnly(libs.luckPerms.api)

    implementation(project(":core-common"))
    implementation(project(":core-persistence"))
    implementation(libs.bundles.cloudPaper)
    implementation(libs.configurate.hocon)
    implementation(libs.invui)
    implementation(libs.lettuce)
    implementation(libs.celestial)
}

tasks {
    val artifact = project.mavenArtifact()

    shadowJar {
        val mapping = mapOf(
            "org.incendo" to "cloud",
            "org.spongepowered.configurate" to "configurate",
            "xyz.xenondevs.invui" to "invui",
            "io.lettuce" to "lettuce",
            "net.crystalixs.celestial" to "celestial"
        )

        val base = "$group.$artifact.paper.libs"
        for ((source, name) in mapping) {
            if (source.startsWith(project.group.toString())) {
                relocate(source, "$base.$name") {
                    exclude("net/crystalixs/core/**")
                }
            } else {
                relocate(source, "$base.$name")
            }
        }
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

    paperPluginYaml {
        val mainClass = project.minecraftPluginMainClass()
        val bootstrapperClass = project.minecraftPluginBootstrapperClass()

        main = "$group.$artifact.paper.$mainClass"
        name = rootProject.property("plugin-name") as String
        authors = project.pluginAuthors()
        apiVersion = "1.21.11"
        bootstrapper = "$group.$artifact.paper.$bootstrapperClass"
        dependencies {
            server("LuckPerms", PaperPluginYaml.Load.BEFORE, required = true, joinClasspath = true)
        }
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
        downloadPlugins {
            url("https://download.luckperms.net/1631/bukkit/loader/LuckPerms-Bukkit-5.5.42.jar")
        }
    }
}

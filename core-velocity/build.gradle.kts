import xyz.jpenilla.runvelocity.task.RunVelocity

plugins {
    alias(libs.plugins.velocityConvention)
    alias(libs.plugins.runVelocity)
    alias(libs.plugins.shadow)
}

dependencies {
    annotationProcessor(libs.velocity)

    compileOnly(libs.velocity)
    compileOnly(libs.brigadier)

    implementation(project(":core-common"))
    implementation(libs.bundles.cloudVelocity)
    implementation(libs.lettuce)
}

tasks {
    val artifact = project.mavenArtifact()

    shadowJar {
        val mapping = mapOf(
            libs.cloud.velocity to "cloud",
            libs.lettuce to "lettuce",
        )

        val base = "$group.$artifact.velocity.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    jar {
        archiveBaseName.set("$artifact-velocity")
    }

    velocityPluginJson {
        val mainClass = project.minecraftPluginMainClass()

        main = "$group.$artifact.velocity.$mainClass"
        name = rootProject.property("plugin-name") as String
        authors = project.pluginAuthors()
        id = artifact
    }

    register<RunVelocity>("runProxy") {
        dependsOn("copyVelocityPlugin")

        velocityVersion(libs.versions.velocity.get())
        doFirst {
            configureVelocityProxy(
                mapOf(
                    "lobby" to DevEnvironment.LOBBY_PORT,
                    "game" to DevEnvironment.GAME_PORT
                )
            )
        }
        downloadPlugins {
            url("https://download.luckperms.net/1624/velocity/LuckPerms-Velocity-5.5.36.jar")
        }
    }

    register<Copy>("copyVelocityPlugin") {
        dependsOn(shadowJar)

        println("Copying plugin into data directory")
        from(shadowJar)
        into(layout.dir(provider { file("run/plugins") }))
    }
}
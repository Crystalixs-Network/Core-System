import xyz.jpenilla.runvelocity.task.RunVelocity

plugins {
    alias(libs.plugins.velocityConvention)
    alias(libs.plugins.runVelocity)
    alias(libs.plugins.shadow)

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotest)
}

dependencies {
    annotationProcessor(libs.velocity)

    compileOnly(libs.velocity)
    compileOnly(libs.brigadier)

    implementation(project(":common"))
    implementation(libs.bundles.cloudVelocity)
    implementation(libs.jackson.databind)
    implementation(libs.gson)
    implementation(libs.configurate)

    testImplementation(libs.bundles.kotlinTest)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
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
            libs.gson to "gson",
            libs.cloud.velocity to "cloud",
            libs.configurate to "configurate"
        )

        val base = "$group.$artifact.velocity.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    kotlin {
        jvmToolchain(21)
    }

    kotest {
        customGradleTask = true
        alwaysRerunTests = true
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
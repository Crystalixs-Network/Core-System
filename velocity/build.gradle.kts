plugins {
    alias(libs.plugins.velocityConvention)
    alias(libs.plugins.runVelocity)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    implementation(project(":common"))
    implementation(libs.bundles.jackson)
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
            libs.jackson.databind to "jackson_databind",
            libs.jackson.bukkit to "jackson_bukkit",
        )

        val base = "$group.$artifact.velocity.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    jar {
        archiveBaseName.set("$artifact-velocity-${rootProject.version}")
    }

    velocityPluginJson {
        val mainClass = project.minecraftPluginMainClass()

        main = "$group.$artifact.velocity.$mainClass"
        name = rootProject.property("plugin-name") as String
        authors = project.pluginAuthors()
    }

    runVelocity {
        velocityVersion("3.5.0-SNAPSHOT")
    }
}
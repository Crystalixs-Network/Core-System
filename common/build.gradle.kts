plugins {
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotest)
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(libs.bundles.jackson)

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
            libs.jackson.databind to "jackson_databind",
            libs.jackson.kotlin to "jackson_kotlin"
        )

        val base = "$group.$artifact.common.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    kotlin {
        jvmToolchain(21)
    }

    test {
        useJUnitPlatform()
    }

    jar {
        archiveBaseName.set("$artifact-common-${rootProject.version}")
    }

    build {
        dependsOn(shadowJar)
    }
}
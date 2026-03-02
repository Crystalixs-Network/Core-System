plugins {
    `java-library`
    `jvm-test-suite`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(libs.bundles.jackson)

    testImplementation(libs.kotlin.stdlib)
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
            libs.jackson.annotations to "jackson_annotations",
            libs.jackson.bukkit to "jackson_bukkit",
        )

        val base = "$group.$artifact.common.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    test {
        useJUnitPlatform()
    }

    check {
        dependsOn(testing.suites.named("jvmTest"))
    }

    jar {
        archiveBaseName.set("$artifact-common-${rootProject.version}")
    }

    build {
        dependsOn(shadowJar)
    }
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        val jvmTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()

            targets {
                all {
                    testTask.configure {
                        testLogging { events("passed", "skipped", "failed") }
                        maxHeapSize = "512M"
                    }
                }
            }
        }
    }
}
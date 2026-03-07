plugins {
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotest)
    alias(libs.plugins.pitest)
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.configurate)
    implementation(libs.cloud.core)

    testImplementation(libs.bundles.kotlinTest)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)

    pitest(libs.pitest.junit5)
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
            libs.jackson.databind to "jackson.databind",
            libs.jackson.kotlin to "jackson.kotlin",
            libs.cloud.core to "cloud",
            libs.configurate.hocon to "configurate.hocon",
            libs.configurate.jackson to "configurate.jackson",
            libs.adventure.api to "adventure.api",
            libs.adventure.text.minimessage to "adventure.text.minimessage",
        )

        val base = "$group.$artifact.common.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    kotlin {
        jvmToolchain(21)
    }

    check {
        dependsOn(pitest)
    }

    jar {
        archiveBaseName.set("$artifact-common")
    }

    build {
        dependsOn(shadowJar)
    }

    kotest {
        customGradleTask = true
        alwaysRerunTests = true
    }

    pitest {
        verbose = true
        detectInlinedCode = true
        outputCharset = Charsets.UTF_8
        threads = 4
        mutationThreshold = 80
        coverageThreshold = 80
        jvmArgs = listOf("-Xmx2G")
        avoidCallsTo = listOf("java.util.logging.*")
        outputFormats = listOf("HTML")
        mutators = listOf("STRONGER")
        targetClasses = listOf("net.crystalixs.core.common.config.ConfigLoader*")
        targetTests = listOf("net.crystalixs.core.common.test.config*")
    }
}
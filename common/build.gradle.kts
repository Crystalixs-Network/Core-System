plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.configurate)
    implementation(libs.cloud.core)
    implementation(libs.slf4j.api)
}

tasks {
    val artifact = project.mavenArtifact()

    shadowJar {
        val mapping = mapOf(
            libs.cloud.core to "cloud",
            libs.configurate.hocon to "configurate.hocon",
            libs.configurate.jackson to "configurate.jackson",
            libs.adventure.api to "adventure.api",
            libs.adventure.text.minimessage to "adventure.text.minimessage",
        )

        val base = "$group.$artifact.common.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    jar {
        archiveBaseName.set("$artifact-common")
    }

    build {
        dependsOn(shadowJar)
    }
}

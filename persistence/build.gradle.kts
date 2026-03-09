plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":common"))
    implementation(libs.mariadb)
    implementation(libs.bundles.sadu)
}

tasks {
    val artifact = project.mavenArtifact()

    shadowJar {
        val mapping = mapOf(
            libs.sadu.datasource to "sadu.datasource",
            libs.sadu.mariadb to "sadu.mariadb",
            libs.sadu.queries to "sadu.queries",
            libs.sadu.updater to "sadu.updater",
            libs.mariadb to "mariadb",
        )

        val base = "$group.$artifact.persistence.libs"
        for ((dependency, name) in mapping) relocate(dependency.get().group, "$base.$name")
    }

    jar {
        archiveBaseName.set("$artifact-persistence")
    }

    build {
        dependsOn(shadowJar)
    }
}
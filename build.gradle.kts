plugins {
    java
}

allprojects {
    group = "net.crystalixs"
    version = project.property("version") as String

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://libraries.minecraft.net")
        maven("https://repo.xenondevs.xyz/releases")
        maven("https://maven.pkg.github.com/Crystalixs-Network/Celestial") {
            credentials {
                username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
                password = (findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

subprojects {
    apply<JavaPlugin>()

    tasks {
        java {
            toolchain.languageVersion.set(JavaLanguageVersion.of(21))
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        compileJava {
            options.encoding = "UTF-8"
            options.release.set(21)
        }

        compileTestJava {
            options.encoding = "UTF-8"
            options.release.set(21)
        }

        withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
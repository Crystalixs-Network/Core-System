package net.crystalixs.core.common.test.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.crystalixs.core.common.config.ConfigLoader
import net.crystalixs.core.common.config.ObjectMapperProvider
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files

class ConfigLoaderTest : FunSpec({

    val mapper = ObjectMapperProvider.mapper()

    test("Pre-Reload State: get() = null") {
        data class TestConfig(val a: Int = 0)

        val tempDir = Files.createTempDirectory("config-loader-pre-reload")
        val configFile = tempDir.resolve("config.json")
        val loader = ConfigLoader(configFile, "default-config.json", TestConfig::class.java)

        loader.get() shouldBe null
    }

    test("Missing File Initialization: reload creates file and persists defaults") {
        data class TestConfig(val a: Int = 0)

        val defaultJson = """{"a":7}"""
        val tempDir = Files.createTempDirectory("config-loader-create-file")
        val configFile = tempDir.resolve("config.json")

        val resourceLoader = object : ClassLoader(ConfigLoader::class.java.classLoader) {
            override fun getResourceAsStream(name: String?): InputStream? {
                return if (name == "default-config.json")
                    ByteArrayInputStream(defaultJson.toByteArray(Charsets.UTF_8))
                else null
            }
        }

        val loader = ConfigLoader(configFile, "default-config.json", TestConfig::class.java, resourceLoader)
        loader.reload()

        Files.exists(configFile) shouldBe true
        loader.get().a shouldBe 7
        mapper.readTree(configFile.toFile()).get("a").asInt() shouldBe 7
    }

    test("Reload Semantics: merged config persisted and in-memory state updated") {
        data class Nested(val x: Int = 0)
        data class TestConfig(val a: Int = 0, val nested: Nested = Nested())

        val defaultJson = """{"a":1,"nested":{"x":1},"unused_default":true}"""
        val userJson = """{"a":42,"extra":999}"""

        val tempDir = Files.createTempDirectory("config-loader-test")
        val configFile = tempDir.resolve("config.json")
        Files.writeString(configFile, userJson)

        val resourceLoader = object : ClassLoader(ConfigLoader::class.java.classLoader) {
            override fun getResourceAsStream(name: String?): InputStream? {
                return if (name == "default-config.json")
                    ByteArrayInputStream(defaultJson.toByteArray(Charsets.UTF_8))
                else null
            }
        }

        val loader = ConfigLoader(configFile, "default-config.json", TestConfig::class.java, resourceLoader)
        loader.reload()

        loader.get().a shouldBe 42
        loader.get().nested.x shouldBe 1

        val persisted = mapper.readTree(configFile.toFile())
        persisted.get("a").asInt() shouldBe 42
        persisted.get("nested").get("x").asInt() shouldBe 1
        persisted.has("extra") shouldBe false
    }

    test("Missing Default Resource: reload throws IOException") {
        data class TestConfig(val a: Int = 0)

        val tempDir = Files.createTempDirectory("config-loader-missing-resource")
        val configFile = tempDir.resolve("config.json")
        val resourceLoader = object : ClassLoader(ConfigLoader::class.java.classLoader) {
            override fun getResourceAsStream(name: String?): InputStream? = null
        }
        val loader = ConfigLoader(configFile, "missing-default.json", TestConfig::class.java, resourceLoader)

        shouldThrow<IOException> { loader.reload() }
    }
})
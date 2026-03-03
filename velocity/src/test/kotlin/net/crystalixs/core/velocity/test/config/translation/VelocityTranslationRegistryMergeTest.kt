package net.crystalixs.core.velocity.test.config.translation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import net.crystalixs.core.velocity.config.translation.VelocityTranslationRegistry
import java.util.Properties

class VelocityTranslationRegistryMergeTest : FunSpec({

    test("∀k ∈ dom(D) \\ dom(U): M(k) = D(k)") {
        val defaults = propertiesOf("a" to "default-a", "b" to "default-b")
        val user = propertiesOf("a" to "user-a")
        val merged = VelocityTranslationRegistry.merge(defaults, user)

        merged.getProperty("a") shouldBe "user-a"
        merged.getProperty("b") shouldBe "default-b"
    }

    test("∀k ∈ dom(D) ∩ dom(U): M(k) = U(k)") {
        val defaults = propertiesOf("motd" to "<green>Default")
        val user = propertiesOf("motd" to "<red>User Override")
        val merged = VelocityTranslationRegistry.merge(defaults, user)

        merged.getProperty("motd") shouldBe "<red>User Override"
    }

    test("dom(M) = dom(D)") {
        val defaults = propertiesOf("known" to "1")
        val user = propertiesOf("known" to "2", "legacy" to "remove-me")
        val merged = VelocityTranslationRegistry.merge(defaults, user)

        merged.stringPropertyNames().toList() shouldContainExactlyInAnyOrder listOf("known")
        merged.getProperty("known") shouldBe "2"
        merged.getProperty("legacy") shouldBe null
    }

    test("U = ∅ ⇒ M = D") {
        val defaults = propertiesOf("x" to "1", "y" to "2")
        val user = propertiesOf()
        val merged = VelocityTranslationRegistry.merge(defaults, user)

        merged.stringPropertyNames().toList() shouldContainExactlyInAnyOrder listOf("x", "y")
        merged.getProperty("x") shouldBe "1"
        merged.getProperty("y") shouldBe "2"
    }

    test("D = ∅ ⇒ M = ∅") {
        val defaults = propertiesOf()
        val user = propertiesOf("x" to "1")
        val merged = VelocityTranslationRegistry.merge(defaults, user)

        merged.stringPropertyNames().isEmpty() shouldBe true
        merged.getProperty("x") shouldBe null
    }

    test("merge(D, merge(D, U)) = merge(D, U") {
        val defaults = propertiesOf("a" to "default-a", "b" to "default-b", "c" to "default-c")
        val user = propertiesOf("a" to "user-a", "legacy" to "remove-me")
        val once = VelocityTranslationRegistry.merge(defaults, VelocityTranslationRegistry.merge(defaults, user))
        val twice = VelocityTranslationRegistry.merge(defaults, once)

        twice.stringPropertyNames().toList() shouldContainExactlyInAnyOrder once.stringPropertyNames().toList()
        once.stringPropertyNames().forEach { key ->
            twice.getProperty(key) shouldBe once.getProperty(key)
        }
    }

    test("dom(merge(D, U)) = dom(D") {
        val defaults = propertiesOf("motd" to "<green>default", "kick.reason" to "<red>maintenance", "prefix" to "<gray>[Core]")
        val user = propertiesOf("motd" to "<gold>custom</gold>", "obsolete.key" to "must-be-removed", "another.legacy" to "must-be-removed-too")
        val merged = VelocityTranslationRegistry.merge(defaults, user)
        val domMerged = merged.stringPropertyNames()
        val domDefaults = defaults.stringPropertyNames()

        domMerged shouldContainExactlyInAnyOrder domDefaults.toList()
    }

})

private fun propertiesOf(vararg entries: Pair<String, String>): Properties = Properties().apply {
    entries.forEach { (key, value) -> setProperty(key, value) }
}
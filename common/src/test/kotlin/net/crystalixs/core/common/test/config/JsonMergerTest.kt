package net.crystalixs.core.common.test.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import net.crystalixs.core.common.config.ConfigLoader
import net.crystalixs.core.common.config.ObjectMapperProvider
import tools.jackson.databind.JsonNode
import tools.jackson.databind.node.JsonNodeFactory
import tools.jackson.databind.node.ObjectNode

class JsonMergerTest : FunSpec({

    val mapper = ObjectMapperProvider.mapper()

    // =============================================================================================================
    // Primitives

    val primitiveArb: Arb<JsonNode> = Arb.Companion.choice(
        Arb.Companion.string().map { JsonNodeFactory.instance.stringNode(it) },
        Arb.Companion.int().map { JsonNodeFactory.instance.numberNode(it.toDouble()) },
        Arb.Companion.boolean().map { JsonNodeFactory.instance.booleanNode(it) }
    )

    // =============================================================================================================
    // Rekursiver JSON Generator (manuell)

    fun jsonNode(depth: Int = 3): Arb<JsonNode> {
        return if (depth <= 0) primitiveArb
        else Arb.Companion.choice(
            primitiveArb, Arb.Companion.map(
                Arb.Companion.string(minSize = 1, maxSize = 3),
                jsonNode(depth - 1),
                minSize = 0,
                maxSize = 3
            ).map { map ->
                val obj = mapper.createObjectNode()
                map.forEach { (k, v) -> obj.set(k, v) }
                obj
            }
        )
    }

    fun jsonObject(depth: Int = 3): Arb<ObjectNode> = jsonNode(depth).filter { it.isObject }.map { it as ObjectNode }

    // =============================================================================================================
    // Referenzmodell

    fun referenceMerge(default: ObjectNode, user: ObjectNode?): ObjectNode {
        val merged = mapper.createObjectNode()
        user ?: return default.deepCopy()

        default.propertyNames().forEach { key ->
            val defaultChild = default.get(key)
            val userChild = user.get(key)

            if (defaultChild.isObject) {
                merged.set(
                    key,
                    if (userChild?.isObject == true)
                        referenceMerge(defaultChild as ObjectNode, userChild as ObjectNode)
                    else defaultChild.deepCopy()
                )

            } else {
                merged.set(key, userChild ?: defaultChild.deepCopy())
            }
        }

        return merged
    }

    // =============================================================================================================
    // Property Tests

    test("Key-Subset: merged.keys ⊆ default.keys") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            merged.propertyNames().forEach { key -> defaults.has(key) shouldBe true }
        }
    }

    test("Default-Komplettheit") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            defaults.propertyNames().forEach { key -> merged.has(key) shouldBe true }
        }
    }

    test("User-Priorität bei Nicht-Objekten") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            defaults.propertyNames().forEach { key ->
                if (user.has(key) && !defaults.get(key).isObject)
                    merged.get(key) shouldBe user.get(key)
            }
        }
    }

    test("Idempotenz") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val once = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            val twice = ConfigLoader.JsonMerger.merge(mapper, once, user)
            twice shouldBe once
        }
    }

    test("Determinismus") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val a = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            val b = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            a shouldBe b
        }
    }

    test("Keine Seiteneffekte") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val defaultsCopy = defaults.deepCopy()
            val userCopy = user.deepCopy()
            ConfigLoader.JsonMerger.merge(mapper, defaults, user)

            defaults shouldBe defaultsCopy
            user shouldBe userCopy
        }
    }

    test("Cross-Check Referenzmodell") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val mergedActual = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            val mergedRef = referenceMerge(defaults, user)
            mergedActual shouldBe mergedRef
        }
    }

    // =============================================================================================================
    // Fixed Tests für deterministische Coverage

    test("Fixed Test: simple override") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = mapper.createObjectNode().put("a", 42)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("a").asInt() shouldBe 42
    }

    test("Fixed Test: missing key in user") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = mapper.createObjectNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("a").asInt() shouldBe 1
    }

    test("Fixed Test: nested object merge") {
        val defaults = mapper.createObjectNode().apply {
            putObject("nested").put("x", 1)
        }
        val user = mapper.createObjectNode().apply {
            putObject("nested").put("x", 2)
        }
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.has("nested") shouldBe true
        merged.get("nested").get("x").asInt() shouldBe 2
    }

    test("Fixed Test: nested object missing in user") {
        val defaults = mapper.createObjectNode().apply {
            putObject("nested").put("x", 1)
        }
        val user = mapper.createObjectNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.has("nested") shouldBe true
        merged.get("nested").get("x").asInt() shouldBe 1
    }

    test("Fixed Test: user extra field removed") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = mapper.createObjectNode().put("a", 42).put("b", 100)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.has("b") shouldBe false
        merged.get("a").asInt() shouldBe 42
    }

    test("Fixed Test: default object with multiple fields") {
        val defaults = mapper.createObjectNode().apply {
            put("x", 1)
            put("y", 2)
        }
        val user = mapper.createObjectNode().put("x", 10)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("x").asInt() shouldBe 10
        merged.get("y").asInt() shouldBe 2
    }

    test("Fixed Test: deep nested merge with missing user keys") {
        val defaults = mapper.createObjectNode().apply {
            putObject("level1").putObject("level2").put("k", 1)
        }
        val user = mapper.createObjectNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.has("level1") shouldBe true
        merged.get("level1").get("level2").get("k").asInt() shouldBe 1
    }

    test("Fixed Test: nested object overridden with primitive") {
        val defaults = mapper.createObjectNode().apply {
            putObject("nested").put("x", 1)
        }
        val user = mapper.createObjectNode().put("nested", 42)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("nested").asInt() shouldBe 42
    }
})
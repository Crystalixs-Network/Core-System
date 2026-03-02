package net.crystalixs.core.common.test.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import net.crystalixs.core.common.config.ConfigLoader
import net.crystalixs.core.common.config.ObjectMapperProvider
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.JsonNodeFactory
import tools.jackson.databind.node.ObjectNode

class JsonMergerTest : FunSpec({

    val mapper = ObjectMapperProvider.mapper()

    // =============================================================================================================
    // Primitives

    val primitiveArb: Arb<JsonNode> = Arb.choice(
        Arb.string().map { JsonNodeFactory.instance.stringNode(it) },
        Arb.int().map { JsonNodeFactory.instance.numberNode(it.toDouble()) },
        Arb.boolean().map { JsonNodeFactory.instance.booleanNode(it) }
    )

    // =============================================================================================================
    // Rekursiver JSON Generator (manuell)

    fun jsonNode(depth: Int = 3): Arb<JsonNode> {
        return if (depth <= 0) primitiveArb
        else Arb.choice(
            primitiveArb, Arb.map(
                Arb.string(minSize = 1, maxSize = 3),
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

    fun referenceMerge(mapper: ObjectMapper, defaultNode: JsonNode, userNode: JsonNode?): JsonNode {
        if (!defaultNode.isObject) {
            return if (userNode != null && !userNode.isNull && !userNode.isMissingNode) {
                userNode.deepCopy()
            } else {
                defaultNode.deepCopy()
            }
        }

        val merged = mapper.createObjectNode()
        val defaultObj = defaultNode as ObjectNode
        val userObj = if (userNode != null && userNode.isObject) userNode as ObjectNode else mapper.createObjectNode()

        defaultObj.propertyNames().forEach { key ->
            val defaultChild = defaultObj.get(key)
            val userChild = userObj.get(key)

            val mergedChild = when {
                userChild == null || userChild.isNull || userChild.isMissingNode -> defaultChild.deepCopy()
                defaultChild.isObject && userChild.isObject -> referenceMerge(mapper, defaultChild, userChild)
                else -> userChild.deepCopy()
            }

            merged.set(key, mergedChild)
        }

        return merged
    }

    // =============================================================================================================
    // Property Tests

    test("Key Set Inclusion: keys(merged) ⊆ keys(defaults)") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            merged.propertyNames().forEach { key -> defaults.has(key) shouldBe true }
        }
    }

    test("Default Key Preservation (Komplettheit): keys(defaults) ⊆ keys(merged)") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            defaults.propertyNames().forEach { key -> merged.has(key) shouldBe true }
        }
    }

    test("Non-Object Override: user(k) replaces default(k)") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            defaults.propertyNames().forEach { key ->
                if (user.has(key) && !defaults.get(key).isObject)
                    merged.get(key) shouldBe user.get(key)
            }
        }
    }

    test("Idempotence: merge(merge(d, u), u) = merge(d, u)") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val once = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            val twice = ConfigLoader.JsonMerger.merge(mapper, once, user)
            twice shouldBe once
        }
    }

    test("Determinism: merge(d, u) is deterministic") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val a = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            val b = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            a shouldBe b
        }
    }

    test("Immutability: merge does not mutate inputs") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val defaultsCopy = defaults.deepCopy()
            val userCopy = user.deepCopy()
            ConfigLoader.JsonMerger.merge(mapper, defaults, user)

            defaults shouldBe defaultsCopy
            user shouldBe userCopy
        }
    }

    test("Reference Equivalence: merge(d, u) = refMerge(d, u)") {
        checkAll(200, jsonObject(), jsonObject()) { defaults, user ->
            val mergedActual = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
            val mergedRef = referenceMerge(mapper, defaults, user)
            mergedActual shouldBe mergedRef
        }
    }

    // =============================================================================================================
    // Fixed Tests für deterministische Coverage

    test("Primitive Override: user primitive overrides default primitive") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = mapper.createObjectNode().put("a", 42)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("a").asInt() shouldBe 42
    }

    test("Missing User Key: merged(k) = default(k)") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = mapper.createObjectNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("a").asInt() shouldBe 1
    }

    test("Recursive Object Merge: object fields merged recursively") {
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

    test("Nested Missing Key Preservation: missing nested user key ⇒ default preserved") {
        val defaults = mapper.createObjectNode().apply {
            putObject("nested").put("x", 1)
        }
        val user = mapper.createObjectNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.has("nested") shouldBe true
        merged.get("nested").get("x").asInt() shouldBe 1
    }

    test("Extra User Key Exclusion: keys(user) \\ keys(defaults) excluded") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = mapper.createObjectNode().put("a", 42).put("b", 100)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.has("b") shouldBe false
        merged.get("a").asInt() shouldBe 42
    }

    test("Partial Override Preservation: non-overridden default fields preserved") {
        val defaults = mapper.createObjectNode().apply {
            put("x", 1)
            put("y", 2)
        }
        val user = mapper.createObjectNode().put("x", 10)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("x").asInt() shouldBe 10
        merged.get("y").asInt() shouldBe 2
    }

    test("Deep Missing Key Preservation: missing deep user keys preserve defaults") {
        val defaults = mapper.createObjectNode().apply {
            putObject("level1").putObject("level2").put("k", 1)
        }
        val user = mapper.createObjectNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.has("level1") shouldBe true
        merged.get("level1").get("level2").get("k").asInt() shouldBe 1
    }

    test("Object-to-Primitive Replacement: user primitive replaces default object") {
        val defaults = mapper.createObjectNode().apply {
            putObject("nested").put("x", 1)
        }
        val user = mapper.createObjectNode().put("nested", 42)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("nested").asInt() shouldBe 42
    }

    test("Non-Object Default Fallback: user = null ⇒ merged = default") {
        val defaultNode = JsonNodeFactory.instance.numberNode(5)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaultNode, null)
        merged shouldBe defaultNode
    }

    test("Null User Field Fallback: user(k) = null ⇒ merged(k) = default(k)") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = mapper.createObjectNode().putNull("a")
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("a").asInt() shouldBe 1
    }

    test("Non-Object User Root Fallback: user root non-object ⇒ merged = defaults over keys(defaults)") {
        val defaults = mapper.createObjectNode().put("a", 1)
        val user = JsonNodeFactory.instance.numberNode(42)
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaults, user)
        merged.get("a").asInt() shouldBe 1
        merged.size() shouldBe 1
    }

    test("Non-Object Default Fallback: user = nullNode ⇒ merged = default") {
        val defaultNode = JsonNodeFactory.instance.numberNode(5)
        val userNode = JsonNodeFactory.instance.nullNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaultNode, userNode)
        merged shouldBe defaultNode
    }

    test("Non-Object Default Fallback: user = missingNode ⇒ merged = default") {
        val defaultNode = JsonNodeFactory.instance.numberNode(5)
        val userNode = JsonNodeFactory.instance.missingNode()
        val merged = ConfigLoader.JsonMerger.merge(mapper, defaultNode, userNode)
        merged shouldBe defaultNode
    }
})
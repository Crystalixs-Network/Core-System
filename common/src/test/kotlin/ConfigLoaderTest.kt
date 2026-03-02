import net.crystalixs.core.common.config.ConfigLoader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class ConfigLoaderTest {

    private lateinit var tmpConfig: Path

    @BeforeEach
    fun setup() {
        tmpConfig = Files.createTempFile("config", ".json")
        Files.delete(tmpConfig)
    }

    data class Motd(val firstLine: String, val secondLine: String)
    data class TestConfig(val motd: Motd, val maintenance: Boolean)

    @Test
    fun `test default file is copied if missing`() {
        val loader = ConfigLoader(tmpConfig, "default-config.json", TestConfig::class.java)

        // Datei existiert noch nicht
        assertFalse { Files.exists(tmpConfig) }

        // Reload kopiert Defaults & erstellt eine merged Config
        loader.reload()
        assertTrue { Files.exists(tmpConfig) }

        val config = loader.get()
        assertEquals("Default First", config.motd.firstLine)
        assertEquals("Default Second", config.motd.secondLine)
        assertFalse(config.maintenance)
    }

    @Test
    fun `test merge user overrides default`() {
        val inputStream = javaClass.classLoader.getResourceAsStream("user-config.json")
            ?: fail("Resource not found: user-config.json")

        // user-config.json wird in tmpConfig kopiert
        Files.copy(inputStream, tmpConfig)

        val loader = ConfigLoader(tmpConfig, "user-config.json", TestConfig::class.java)
        loader.reload()

        val config = loader.get()

        // User überschreibt Default
        assertEquals("User First", config.motd.firstLine)

        // Default bleibt, wenn User keinen Wert setzt
        assertEquals("Default Second", config.motd.secondLine)

        // User überschreibt Default
        assertTrue(config.maintenance)
    }
}
package no.jstien.roi.util

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class FileUtilTest {
    @Test
    fun `recursiveDelete throws if subject is not child of root`() {
        assertThrows(RuntimeException::class.java) {
            recursiveDelete(File("/tmp/sneaky_delety/c"))
        }
    }

    @Test
    fun `recursiveDelete throws if subect contains path traversal tomfoolery`() {
        assertThrows(RuntimeException::class.java) {
            recursiveDelete(File("$ROOT_TEMP_DIRECTORY/a/../"))
        }
    }

    @Test
    fun `recursiveDelete deletes recursively`() {
        val dirA = File("$ROOT_TEMP_DIRECTORY/a")
        dirA.mkdirs()
        dirA.exists() shouldBe true

        val dirB = File("$ROOT_TEMP_DIRECTORY/a")
        dirB.mkdirs()
        dirB.exists() shouldBe true

        recursiveDelete(File("$ROOT_TEMP_DIRECTORY"))

        dirA.exists() shouldBe false
        dirB.exists() shouldBe false
    }
}
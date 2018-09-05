package no.jstien.jrkserver.util

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class FileUtilTest {
    @Test
    fun `recursiveDelete throws if subject is not child of root`() {
        val fileUtil = FileUtil("/tmp/a/")
        assertThrows(RuntimeException::class.java) {
            fileUtil.recursiveDelete(File("/tmp/b/c"))
        }
    }

    @Test
    fun `recursiveDelete throws if subect contains path traversal tomfoolery`() {
        val fileUtil = FileUtil("/tmp/a/")
        assertThrows(RuntimeException::class.java) {
            fileUtil.recursiveDelete(File("/tmp/a/../"))
        }
    }

    @Test
    fun `recursiveDelete deletes recursively`() {
        val dirA = File("/tmp/__tmpdir/a")
        dirA.mkdirs()
        dirA.exists() shouldBe true

        val dirB = File("/tmp/__tmpdir/a")
        dirB.mkdirs()
        dirB.exists() shouldBe true

        val fileUtil = FileUtil("/tmp/__tmpdir")
        fileUtil.recursiveDelete(File("/tmp/__tmpdir/"))

        dirA.exists() shouldBe false
        dirB.exists() shouldBe false
    }
}
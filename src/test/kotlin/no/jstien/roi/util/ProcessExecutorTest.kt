package no.jstien.roi.util

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProcessExecutorTest {
    private var executor = ProcessExecutor()

    @BeforeEach
    fun init() {
        executor = ProcessExecutor()
    }

    @Test
    fun `bogus commands throws`() {
        // If this is executable on your path, that's your problem
        assertThrows(RuntimeException::class.java) {
            executor.execute("artlkjhaertknbasrtk")
        }
    }

    @Test
    fun `successful command returns 0 status code`() {
        executor.execute("echo hei mamma")
        executor.exitCode shouldBe 0
    }

    @Test
    fun `unsuccessful command returns non-0 status code`() {
        executor.execute("ls --phartiphokborlz")
        executor.exitCode shouldNotBe 0
    }

    @Test
    fun `multi-line stdout is properly capture`() {
        executor.execute("printf heisann\\nsveisann")

        executor.exitCode shouldBe 0
        executor.stderr.size shouldBe 0
        executor.stdout.size shouldBe 2
        executor.stdout[0] shouldBe("heisann")
        executor.stdout[1] shouldBe("sveisann")
    }
}
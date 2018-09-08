package no.jstien.jrkserver.episodes

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

internal class EpisodeSegmentTest {
    @Test
    fun `id is increments globally across instances`() {
        val seg1 = EpisodeSegment(100, 40.0, "/tmp/pls")
        val seg2 = EpisodeSegment(101, 40.0, "/tmp/pls")
        val seg3 = EpisodeSegment(400, 1.0, "/tmp/lol")

        seg2.id shouldBe seg1.id + 1
        seg3.id shouldBe seg2.id + 1
    }
}
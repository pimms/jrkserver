package no.jstien.jrkserver.episodes

import io.kotlintest.shouldBe
import no.jstien.jrkserver.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EpisodeTest {
    @Test
    fun `episode length is determined by sum of segments`() {
        val segs = List(10) { n -> EpisodeSegment(n, 9.5, "/tmp/lol$n.mp3") }
        val episode = Episode("$ROOT_TEMP_DIRECTORY/pls", segs)

        episode.length shouldBe 95.0
    }
}
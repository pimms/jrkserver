package no.jstien.jrk.live.episodes

import io.kotlintest.shouldBe
import no.jstien.jrk.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.Test

class StreamableEpisodeTest {
    @Test
    fun `episode length is determined by sum of segments`() {
        val segs = List(10) { n -> EpisodeSegment(n, 9.5, "/tmp/lol$n.mp3") }
        val episode = StreamableEpisode("$ROOT_TEMP_DIRECTORY/pls", segs)

        episode.length shouldBe 95.0
    }
}
package no.jstien.jrkserver.stream

import io.kotlintest.shouldBe
import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.episodes.EpisodeSegment
import no.jstien.jrkserver.stream.EpisodeStream.Companion.NANOS_PER_SEC
import no.jstien.jrkserver.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DefaultEpisodeStreamTest {
    private val SEGMENT_COUNT = 50
    private val SEGMENT_LEN = 15.0

    private var episode: Episode = createEpisode()


    private fun createEpisode(): Episode {
        val segs = Array(SEGMENT_COUNT) { i -> EpisodeSegment(i, SEGMENT_LEN, "$ROOT_TEMP_DIRECTORY/lul/$i.mp3") }
        return Episode("$ROOT_TEMP_DIRECTORY/lul", segs.toList())
    }


    @Test
    fun `episode end time is properly calculated when startTime is in the future`() {
        val streamer = DefaultEpisodeStream(episode)
        val episodeDurationNs = episode.length.toLong() * NANOS_PER_SEC

        streamer.setStartAvailability(500)
        val remaining = streamer.getRemainingTime(0)

        remaining shouldBe episodeDurationNs + 500L
    }

    @Test
    fun `episode end time is negative when in the past`() {
        val streamer = DefaultEpisodeStream(episode)
        val episodeDurationNs = episode.length.toLong() * NANOS_PER_SEC

        streamer.setStartAvailability(1L * NANOS_PER_SEC)
        val remaining = streamer.getRemainingTime(episodeDurationNs + 10L * NANOS_PER_SEC)

        remaining shouldBe -9L * NANOS_PER_SEC
    }


    @Test
    fun `segments lasts at least as long as availability interval`() {
        val streamer = DefaultEpisodeStream(episode)
        streamer.setStartAvailability(10L * NANOS_PER_SEC)

        for (i in 1 until SEGMENT_LEN.toInt() * 2) {
            val availableSegs = streamer.getAvailableSegments(i, 10L* NANOS_PER_SEC)
            availableSegs.sumByDouble { it.length } >= i.toDouble()
        }
    }

    @Test
    fun `additional segments are addded as time progresses`() {
        val streamer = DefaultEpisodeStream(episode)
        var now = 10L * NANOS_PER_SEC

        streamer.setStartAvailability(now)

        var segs = streamer.getAvailableSegments(14, now)
        segs.size shouldBe 1
        segs[0].index shouldBe 0

        now = 20L * NANOS_PER_SEC
        segs = streamer.getAvailableSegments(14, now)
        segs.size shouldBe 2
        segs[0].index shouldBe 0
        segs[1].index shouldBe 1

        now = 27L * NANOS_PER_SEC
        segs = streamer.getAvailableSegments(14, now)
        segs.size shouldBe 2
        segs[0].index shouldBe 1
        segs[1].index shouldBe 2

    }
}
package no.jstien.roi.stream

import io.kotlintest.shouldBe
import no.jstien.roi.episodes.StreamableEpisode
import no.jstien.roi.episodes.EpisodeSegment
import no.jstien.roi.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.Test

internal class DefaultEpisodeStreamTest {
    private val SEGMENT_COUNT = 50
    private val SEGMENT_LEN = 15.0

    private var streamableEpisode: StreamableEpisode = createEpisode()


    private fun createEpisode(): StreamableEpisode {
        val segs = Array(SEGMENT_COUNT) { i -> EpisodeSegment(i, SEGMENT_LEN, "$ROOT_TEMP_DIRECTORY/lul/$i.mp3") }
        return StreamableEpisode("$ROOT_TEMP_DIRECTORY/lul", segs.toList())
    }


    @Test
    fun `episode end time is properly calculated when startTime is in the future`() {
        val streamer = DefaultEpisodeStream(streamableEpisode)
        val episodeDurationNs = streamableEpisode.length.toLong()

        streamer.setStartAvailability(0.5)
        val remaining = streamer.getRemainingTime(0.0)

        remaining shouldBe episodeDurationNs + 0.5
    }

    @Test
    fun `episode end time is negative when in the past`() {
        val streamer = DefaultEpisodeStream(streamableEpisode)
        val episodeDuration = streamableEpisode.length.toLong()

        streamer.setStartAvailability(1.0)
        val remaining = streamer.getRemainingTime(episodeDuration + 10.0)

        remaining shouldBe -9.0
    }


    @Test
    fun `segments lasts at least as long as availability interval`() {
        val streamer = DefaultEpisodeStream(streamableEpisode)
        streamer.setStartAvailability(10.0)

        for (i in 1 until SEGMENT_LEN.toInt() * 2) {
            val availableSegs = streamer.getAvailableSegments(i.toDouble(), 10.0)
            availableSegs.sumByDouble { it.length } >= i.toDouble()
        }
    }

    @Test
    fun `additional segments are addded as time progresses`() {
        val streamer = DefaultEpisodeStream(streamableEpisode)
        var now = 10.0

        streamer.setStartAvailability(now)

        var segs = streamer.getAvailableSegments(14.0, now)
        segs.size shouldBe 1
        segs[0].index shouldBe 0

        now = 20.0
        segs = streamer.getAvailableSegments(14.0, now)
        segs.size shouldBe 2
        segs[0].index shouldBe 0
        segs[1].index shouldBe 1

        now = 27.0
        segs = streamer.getAvailableSegments(14.0, now)
        segs.size shouldBe 2
        segs[0].index shouldBe 1
        segs[1].index shouldBe 2

    }
}
package no.jstien.jrkserver.stream

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.episodes.EpisodeSegment
import no.jstien.jrkserver.episodes.S3Downloader
import no.jstien.jrkserver.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class InfiniteEpisodeStreamTest {
    private val SEGMENT_DURATION = 10.0
    private val SEGMENTS_PER_EPISODE = 6

    private val s3Downloader = mockk<S3Downloader>()
    private var episodeStream = InfiniteEpisodeStream(s3Downloader)

    @BeforeEach
    fun setup() {
        val segs = Array<EpisodeSegment>(SEGMENTS_PER_EPISODE) {
            i -> EpisodeSegment(i, SEGMENT_DURATION, ROOT_TEMP_DIRECTORY + "/lel/$i.mp3")
        }
        every {
            s3Downloader.getNextEpisode()
        } returns Episode(ROOT_TEMP_DIRECTORY + "/lel", segs.toList())

        episodeStream = InfiniteEpisodeStream(s3Downloader)
    }


    @Test
    fun `segments are returned from one episode if possible`() {
        val now = EpisodeStream.NANOS_PER_SEC

        episodeStream.setStartAvailability(now)
        val segs = episodeStream.getAvailableSegments(55, now)

        segs.size shouldBe 6

        // We know that the segments are from the same episode, because they will have
        // an increasing index.
        for (i in 0 until 6) {
            segs[i].index shouldBe i
        }
    }

}
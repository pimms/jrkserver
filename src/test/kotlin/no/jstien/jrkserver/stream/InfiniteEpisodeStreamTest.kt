package no.jstien.jrkserver.stream

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.episodes.repo.EpisodeRepository
import no.jstien.jrkserver.episodes.EpisodeSegment
import no.jstien.jrkserver.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class InfiniteEpisodeStreamTest {
    private val SEGMENT_DURATION = 10.0
    private val SEGMENTS_PER_EPISODE = 6

    private val episodeRepository = mockk<EpisodeRepository>()
    private var episodeStream = InfiniteEpisodeStream(episodeRepository)

    @BeforeEach
    fun setup() {
        every {
            episodeRepository.getNextEpisode()
        } answers { createEpisode() }

        episodeStream = InfiniteEpisodeStream(episodeRepository)
    }

    private fun createEpisode(): Episode {
        val segs = Array<EpisodeSegment>(SEGMENTS_PER_EPISODE) {
            i -> EpisodeSegment(i, SEGMENT_DURATION, ROOT_TEMP_DIRECTORY + "/lel/$i.mp3")
        }
        return Episode(ROOT_TEMP_DIRECTORY + "/lel", segs.toList())
    }


    @Test
    fun `segments are returned from one episode if possible`() {
        val now = 10.0

        episodeStream.setStartAvailability(now)
        val segs = episodeStream.getAvailableSegments(55.0, now)

        segs.size shouldBe 6

        // We know that the segments are from the same episode, because they will have
        // an increasing index.
        for (i in 0 until 6) {
            segs[i].index shouldBe i
        }
    }

    @Test
    fun `segments are returned from multiple episodes if needed`() {
        val now = 35.0

        episodeStream.setStartAvailability(0.0)
        val segs = episodeStream.getAvailableSegments(60.0, now)

        segs.size shouldBe 7
        segs[0].index shouldBe 3
        segs[1].index shouldBe 4
        segs[2].index shouldBe 5
        segs[3].index shouldBe 0
        segs[4].index shouldBe 1
        segs[5].index shouldBe 2
        segs[6].index shouldBe 3
    }

    @Test
    fun `expired episodes are discarded after expiry`() {
        episodeStream.setStartAvailability(0.0)

        val firstSegment = episodeStream.getAvailableSegments(10.0, 0.0)[0]
        val secondSegment = episodeStream.getAvailableSegments(10.0, 61.0)[0]

        firstSegment.index shouldBe 0
        secondSegment.index shouldBe 0
        firstSegment.id shouldNotBe secondSegment.id
    }

    @Test
    fun `arbitrary episodes can be consecutively streamed`() {
        episodeStream.setStartAvailability(0.0)
        val segs = episodeStream.getAvailableSegments(179.0, 0.0)

        segs.size shouldBe 18

        for (i in 1..17) {
            segs[i].id shouldBe segs[i-1].id + 1
        }
    }
}
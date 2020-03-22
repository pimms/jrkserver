package no.jstien.jrk.live.episodes.repo

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import no.jstien.jrk.S3FileRepository
import no.jstien.jrk.live.episodes.StreamableEpisode
import no.jstien.jrk.live.episodes.EpisodeSegment
import no.jstien.jrk.live.episodes.MetadataExtractor
import no.jstien.jrk.live.episodes.segmentation.FFMPEGSegmenter
import no.jstien.jrk.event.EventLog
import no.jstien.jrk.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val S3_KEY = "20161231.mp3"

class EpisodeRepositoryTest {
    private val fileRepository = mockk<S3FileRepository>()
    private val eventLog = mockk<EventLog>(relaxed = true)
    private var metadataExtractor = MetadataExtractor(null)
    private var episodeRepository: EpisodeRepository? = null

    @BeforeEach
    fun setup() {
        mockkConstructor(FFMPEGSegmenter::class)

        every { fileRepository.popRandomS3Key() } returns S3_KEY
        every { fileRepository.downloadFile(any()) } returns "$ROOT_TEMP_DIRECTORY/downloaded.mp3"

        val segs = List(10) { n -> EpisodeSegment(n, 10.0, "/tmp/lol$n.mp3") }
        val episode = StreamableEpisode("$ROOT_TEMP_DIRECTORY/pls", segs)
        every { anyConstructed<FFMPEGSegmenter>().segmentFile(any()) } returns episode

        episodeRepository = EpisodeRepository(fileRepository, metadataExtractor, eventLog)
    }

    @Test
    fun `episode repository enriches episode meta with season`() {
        val episode = episodeRepository!!.getNextEpisode()
        episode.season shouldBe "2016"
    }

    @Test
    fun `episode repository enriches episode meta with display name`() {
        val episode = episodeRepository!!.getNextEpisode()
        episode.displayName shouldBe "Lørdag 31/12"
    }
}
package no.jstien.jrk.live.episodes.segmentation

import io.kotlintest.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.jstien.jrk.util.ProcessExecutor
import no.jstien.jrk.util.ROOT_TEMP_DIRECTORY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileWriter

class FFMPEGSegmenterTest {
    private val executor = mockk<ProcessExecutor>(relaxUnitFun = true)

    @BeforeEach
    fun setup() {
        clearMocks(executor)
    }

    @Test
    fun `constructor throws if FFMPEG returns unexpected format`() {
        every { executor.stdout } returns arrayListOf("i am not ffmpeg tho")
        every { executor.exitCode } returns 0
        assertThrows(RuntimeException::class.java) {
            FFMPEGSegmenter(executor)
        }

        verify(exactly=1) { executor.execute("ffmpeg -version") }
    }

    @Test
    fun `constructor throws if FFMPEG returns non-zero status code`() {
        every { executor.stdout } returns arrayListOf("ffmpeg version 4.0.1 copyright whoever makes ffmpeg")
        every { executor.exitCode } returns 1

        assertThrows(RuntimeException::class.java) {
            FFMPEGSegmenter(executor)
        }

        verify(exactly=1) { executor.execute("ffmpeg -version") }
    }

    @Test
    fun `constructor does not throw if FFMPEG returns expected version format`() {
        every { executor.stdout } returns arrayListOf("ffmpeg version 4.0.1 copyright whoever makes ffmpeg")
        every { executor.exitCode } returns 0

        val segmenter = FFMPEGSegmenter(executor)
        verify(exactly=1) { executor.execute("ffmpeg -version") }
    }


    @Test
    fun `segmentFile MOAT`() {
        val rootDir = "$ROOT_TEMP_DIRECTORY/segmentation_0"

        // Prepare the m3u8 file
        FFMPEGSegmenter.nextId = 0

        File("$rootDir/").mkdirs()
        val writer = FileWriter("$rootDir/seg.m3u8", false)
        writer.write("#EXTINF:13.5\n")
        writer.write("lol.mp3\n")
        writer.write("#EXTINF:5\n")
        writer.write("lol2.mp3\n")
        writer.flush()
        FileWriter("$rootDir/lol.mp3").run { write("hei"); flush() }
        FileWriter("$rootDir/lol2.mp3").run { write("hei"); flush() }

        every { executor.exitCode } returns 0
        every { executor.stdout } returns arrayListOf("ffmpeg version 4.0.5")

        val request = SegmentationRequest(11, "/somewhere/cool/what.mp3", "20191231.mp3")
        val segmenter = FFMPEGSegmenter(executor)
        val episode = segmenter.segmentFile(request)

        episode.segmentCount shouldBe 2

        episode.getSegment(0).filePath shouldBe "$rootDir/lol.mp3"
        episode.getSegment(1).filePath shouldBe "$rootDir/lol2.mp3"

        episode.getSegment(0).index shouldBe 0
        episode.getSegment(1).index shouldBe 1

        episode.getSegment(0).length shouldBe 13.5
        episode.getSegment(1).length shouldBe 5.0
    }

}
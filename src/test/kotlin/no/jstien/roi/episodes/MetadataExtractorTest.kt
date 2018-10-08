package no.jstien.roi.episodes

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

class MetadataExtractorTest {
    @Test
    fun `blank season prefix is ignored`() {
        val extractor = MetadataExtractor("   ")
        val meta = extractor.extractFromS3Key("20160101.mp3")
        meta.season shouldBe "2016"
    }

    @Test
    fun `null season prefix is ignored`() {
        val extractor = MetadataExtractor(null)
        val meta = extractor.extractFromS3Key("20160101.mp3")
        meta.season shouldBe "2016"
    }

    @Test
    fun `defined season prefix is included`() {
        val extractor = MetadataExtractor("Radioshow")
        val meta = extractor.extractFromS3Key("20160101.mp3")
        meta.season shouldBe "Radioshow 2016"
    }


    @Test
    fun `display name is properly parsed`() {
        val extractor = MetadataExtractor("Radioshow")
        val meta = extractor.extractFromS3Key("20160101.mp3")
        meta.displayName shouldBe "Fredag 1/1"
    }

}
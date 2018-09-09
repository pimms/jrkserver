package no.jstien.jrkserver.episodes

import no.jstien.jrkserver.episodes.Episode.Companion.TARGET_SEGMENT_DURATION
import no.jstien.jrkserver.episodes.segmentation.FFMPEGSegmenter
import no.jstien.jrkserver.util.ProcessExecutor
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class EpisodeRepository(fileRepository: S3FileRepository) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private val episodeRepository = fileRepository
    private var episodeTask = startNextDownload()

    fun getNextEpisode(): Episode {
        val episode = episodeTask.get()
        episodeTask = startNextDownload()
        return episode
    }

    private fun startNextDownload(): CompletableFuture<Episode> {
        return CompletableFuture.supplyAsync { downloadEpisode() }
    }

    private fun downloadEpisode(): Episode {
        LOGGER.info("Starting preparation of episode")

        val s3Key = episodeRepository.popRandomS3Key()
        val path = episodeRepository.downloadFile(s3Key)

        val segmentationRequest = SegmentationRequest(TARGET_SEGMENT_DURATION, path, s3Key)
        val segmenter = FFMPEGSegmenter(ProcessExecutor())
        val episode = segmenter.segmentFile(segmentationRequest)

        LOGGER.info("Episode prepared")
        return episode
    }

}
package no.jstien.roi.episodes.repo

import no.jstien.roi.episodes.StreamableEpisode
import no.jstien.roi.episodes.StreamableEpisode.Companion.TARGET_SEGMENT_DURATION
import no.jstien.roi.episodes.MetadataExtractor
import no.jstien.roi.episodes.segmentation.FFMPEGSegmenter
import no.jstien.roi.episodes.segmentation.SegmentationRequest
import no.jstien.roi.event.Event
import no.jstien.roi.event.EventLog
import no.jstien.roi.util.ProcessExecutor
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class EpisodeRepository(fileRepository: S3FileRepository, metadataExtractor: MetadataExtractor, eventLog: EventLog) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private val episodeRepository = fileRepository
    private val metadataExtractor = metadataExtractor
    private val eventLog: EventLog = eventLog

    private var episodeTask = startNextDownload()


    fun getNextEpisode(): StreamableEpisode {
        val episode = episodeTask.get()
        episodeTask = startNextDownload()
        return episode
    }

    private fun startNextDownload(): CompletableFuture<StreamableEpisode> {
        return CompletableFuture.supplyAsync { downloadEpisode() }
    }

    private fun downloadEpisode(): StreamableEpisode {
        LOGGER.info("Starting preparation of streamableEpisode")

        val s3Key = episodeRepository.popRandomS3Key()
        eventLog.addEvent(Event.Type.SERVER_EVENT, "Download started", "S3-key '${s3Key}'")

        val path = episodeRepository.downloadFile(s3Key)

        eventLog.addEvent(Event.Type.SERVER_EVENT, "Segmentation started", "S3-key '${s3Key}'")
        val segmentationRequest = SegmentationRequest(TARGET_SEGMENT_DURATION, path, s3Key)
        val segmenter = FFMPEGSegmenter(ProcessExecutor())
        val episode = segmenter.segmentFile(segmentationRequest)
        episode._meta = metadataExtractor.extractFromS3Key(s3Key)

        eventLog.addEvent(Event.Type.SERVER_EVENT, "Segmentation completed", "S3-key '${s3Key}'")
        LOGGER.info("StreamableEpisode prepared")
        return episode
    }

}

package no.jstien.jrk.live.episodes.repo

import no.jstien.jrk.event.Event
import no.jstien.jrk.event.EventLog
import no.jstien.jrk.live.episodes.MetadataExtractor
import no.jstien.jrk.live.episodes.StreamableEpisode
import no.jstien.jrk.live.episodes.StreamableEpisode.Companion.TARGET_SEGMENT_DURATION
import no.jstien.jrk.live.episodes.segmentation.FFMPEGSegmenter
import no.jstien.jrk.live.episodes.segmentation.SegmentationRequest
import no.jstien.jrk.live.stream.StreamFileRepository
import no.jstien.jrk.util.ProcessExecutor
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class EpisodeRepository(streamFileRepository: StreamFileRepository, metadataExtractor: MetadataExtractor, eventLog: EventLog) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private val episodeRepository = streamFileRepository
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

package no.jstien.jrk.persistence

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader
import no.jstien.jrk.S3FileRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File

@Component
class MetadataGenerator
    @Autowired constructor(
            private val persistentEpisodeRepository: PersistentEpisodeRepository,
            private val s3FileRepository: S3FileRepository
    ) {

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private var persistentEpisodes: List<PersistentEpisode>? = null

    @Scheduled(fixedRate = 86400_000)
    fun generateMetadata() {
        LOGGER.info("Refreshing metadata")
        persistentEpisodeRepository.getAllEpisodes { persistentEpisodes ->
            this.persistentEpisodes = persistentEpisodes
            val s3Keys = s3FileRepository.getAllFileNames()

            s3Keys.forEach { s3Key ->
                generateMetadataForEpisode(s3Key)
            }
        }
    }

    private fun generateMetadataForEpisode(s3Key: String) {
        LOGGER.info("$s3Key...")

        var episode = persistentEpisodes?.filter { e -> e.s3Key == s3Key }?.firstOrNull()

        if (episode == null) {
            episode = PersistentEpisode(s3Key, null)
            LOGGER.info("  Creating entry")
            persistentEpisodeRepository.saveEpisode(episode)
        }

        if (episode.duration == null) {
            val filePath = s3FileRepository.downloadFile(s3Key)

            try {
                val file = File(filePath)
                val fileFormat = MpegAudioFileReader().getAudioFileFormat(file)
                val properties = fileFormat.properties()
                val duration = (properties["duration"] as Long / 1_000_000).toInt()

                episode = PersistentEpisode(s3Key, duration)
                persistentEpisodeRepository.saveEpisode(episode)
            } catch (e: Exception) {
                LOGGER.error("Failed to extract duration", e)
            }
        }
    }
}
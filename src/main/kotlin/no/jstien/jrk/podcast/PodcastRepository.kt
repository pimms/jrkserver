package no.jstien.jrk.podcast

import no.jstien.jrk.S3FileRepository
import no.jstien.jrk.live.episodes.EpisodeMetadata
import no.jstien.jrk.persistence.MetadataExtractor
import no.jstien.jrk.persistence.PersistentEpisode
import no.jstien.jrk.persistence.PersistentEpisodeRepository
import org.springframework.format.datetime.DateFormatter
import org.springframework.scheduling.annotation.Scheduled
import java.net.URLEncoder
import java.util.*

class PodcastRepository(
        private val s3FileRepo: S3FileRepository,
        private val podcastManifest: PodcastManifest,
        private val persistentEpisodeRepository: PersistentEpisodeRepository
) {
    private val metadataExtractor: MetadataExtractor = MetadataExtractor(seasonPrefix = null)
    private val pubdateFormatter = DateFormatter("EEE, dd MMM yyyy HH:mm:ss zzz");
    private var persistentEpisodes: List<PersistentEpisode>? = null

    fun getFeed(): PodcastFeed {
        return PodcastFeed(
                podcastManifest.title,
                podcastManifest.description,
                podcastManifest.link,
                podcastManifest.imageUrl,
                getItems())
    }

    private fun getItems(): List<PodcastItem> {
        // TODO: Merge in upstream episodes
        return getS3Episodes()
    }

    private fun getS3Episodes(): List<PodcastItem> {
        val references = s3FileRepo.getAllReferences()
        return references
                .map { metadataExtractor.extractFromS3Reference(it) }
                .map {
                    PodcastItem(
                            it.displayName,
                            getPersistentEpisodeForS3Key(it.s3Key)?.desc ?: "",
                            getPubdate(it),
                            Enclosure(getDownloadUrl(it), it.size),
                            getDuration(it))
                }
    }

    private fun getPubdate(metadata: EpisodeMetadata): String {
        return pubdateFormatter.print(metadata.date, Locale.getDefault())
    }

    private fun getDownloadUrl(metadata: EpisodeMetadata): String {
        val encodedKey = URLEncoder.encode(metadata.s3Key, "UTF-8")
        return "${podcastManifest.rootUrl}/podcast/episode/${encodedKey}"
    }

    private fun getDuration(metadata: EpisodeMetadata): Int? {
        val episode = getPersistentEpisodeForS3Key(metadata.s3Key)
        return episode?.duration
    }

    private fun getPersistentEpisodeForS3Key(s3Key: String): PersistentEpisode? {
        return persistentEpisodes?.find { e -> e.s3Key == s3Key }
    }

    @Scheduled(fixedRate = 3600_000)
    private fun refreshMetadata() {
        persistentEpisodeRepository.getAllEpisodes { episodes ->
            persistentEpisodes = episodes
        }
    }
}
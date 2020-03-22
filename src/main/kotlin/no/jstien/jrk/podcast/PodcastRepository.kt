package no.jstien.jrk.podcast

import no.jstien.jrk.live.episodes.EpisodeMetadata
import no.jstien.jrk.live.episodes.MetadataExtractor
import no.jstien.jrk.live.episodes.repo.S3FileRepository
import org.springframework.format.datetime.DateFormatter
import java.util.*

class PodcastRepository(
        private val s3FileRepo: S3FileRepository,
        private val podcastManifest: PodcastManifest
) {
    private val metadataExtractor: MetadataExtractor = MetadataExtractor(seasonPrefix = null)
    private val pubdateFormatter = DateFormatter("MMM dd, yyyy");

    fun getFeed(): PodcastFeed {
        return PodcastFeed(podcastManifest.title, podcastManifest.description, getItems())
    }

    private fun getItems(): List<PodcastItem> {
        // TODO: Merge in upstream episodes
        return getS3Episodes()
    }


    private fun getS3Episodes(): List<PodcastItem> {
        val s3Keys = s3FileRepo.getAllFileNames()
        return s3Keys
                .map { metadataExtractor.extractFromS3Key(it) }
                .map { PodcastItem(it.displayName, it.description, getPubdate(it), Enclosure(getDownloadUrl(it))) }
    }

    private fun getPubdate(metadata: EpisodeMetadata): String {
        return pubdateFormatter.print(metadata.date, Locale.getDefault())
    }

    private fun getDownloadUrl(metadata: EpisodeMetadata): String {
        return "${podcastManifest.rootUrl}/podcast/episode/${metadata.s3Key}"
    }
}
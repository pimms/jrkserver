package no.jstien.jrk.podcast

import no.jstien.jrk.S3FileRepository
import no.jstien.jrk.persistence.PersistentEpisodeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@PropertySource(value= ["classpath:config.properties"])
open class PodcastConfiguration {
    @Value("\${podcast.title}") private val podcastTitle: String? = null
    @Value("\${podcast.description}") private val podcastDesc: String? = null
    @Value("\${podcast.rooturl}") private val podcastRootUrl: String? = null
    @Value("\${podcast.imageUrl}") private val podcastImageUrl: String? = null

    @Bean
    @Autowired
    open fun podcastRepository(
            s3FileRepository: S3FileRepository,
            persistentEpisodeRepository: PersistentEpisodeRepository
    ): PodcastRepository {
        val podcastManifest = PodcastManifest(podcastTitle!!, podcastDesc!!, podcastRootUrl!!, podcastImageUrl!!)
        return PodcastRepository(s3FileRepository, podcastManifest, persistentEpisodeRepository)
    }
}
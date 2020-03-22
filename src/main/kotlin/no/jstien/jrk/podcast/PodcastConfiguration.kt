package no.jstien.jrk.podcast

import no.jstien.jrk.live.episodes.repo.S3FileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value= ["classpath:config.properties"])
open class PodcastConfiguration {
    @Value("\${podcast.title") private val podcastTitle: String? = null
    @Value("\${podcast.description") private val podcastDesc: String? = null
    @Value("\${podcast.rooturl") private val podcastRootUrl: String? = null

    @Bean
    @Autowired
    open fun podcastRepository(s3FileRepository: S3FileRepository): PodcastRepository {
        val podcastManifest = PodcastManifest(podcastTitle!!, podcastDesc!!, podcastRootUrl!!)
        return PodcastRepository(s3FileRepository, podcastManifest)
    }
}
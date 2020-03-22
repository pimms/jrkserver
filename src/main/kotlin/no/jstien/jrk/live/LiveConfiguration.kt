package no.jstien.jrk.live

import no.jstien.jrk.S3FileRepository
import no.jstien.jrk.event.EventLog
import no.jstien.jrk.live.episodes.MetadataExtractor
import no.jstien.jrk.live.episodes.repo.EpisodeRepository
import no.jstien.jrk.live.stream.InfiniteEpisodeStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "live", name = ["enabled"])
@PropertySource("classpath:config.properties")
open class LiveConfiguration {
    @Bean
    @Autowired
    open fun metadataExtractor(@Value("\${meta.seasonPrefix}") seasonPrefix: String): MetadataExtractor {
        return MetadataExtractor(seasonPrefix)
    }

    @Bean
    @Autowired
    open fun episodeRepository(metadataExtractor: MetadataExtractor,
                               s3FileRepository: S3FileRepository,
                               eventLog: EventLog): EpisodeRepository {
        return EpisodeRepository(s3FileRepository, metadataExtractor, eventLog)
    }

    @Bean
    @Autowired
    open fun infiniteEpisodeStream(episodeRepository: EpisodeRepository,
                                   eventLog: EventLog): InfiniteEpisodeStream {
        return InfiniteEpisodeStream(episodeRepository, eventLog)
    }
}
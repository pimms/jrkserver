package no.jstien.jrk.live

import no.jstien.jrk.S3FileRepository
import no.jstien.jrk.event.EventLog
import no.jstien.jrk.persistence.MetadataExtractor
import no.jstien.jrk.live.episodes.repo.EpisodeRepository
import no.jstien.jrk.live.stream.InfiniteEpisodeStream
import no.jstien.jrk.live.stream.StreamFileRepository
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
    open fun streamFileRepository(s3FileRepository: S3FileRepository): StreamFileRepository {
        return StreamFileRepository(s3FileRepository)
    }

    @Bean
    @Autowired
    open fun metadataExtractor(@Value("\${meta.seasonPrefix}") seasonPrefix: String): MetadataExtractor {
        return MetadataExtractor(seasonPrefix)
    }

    @Bean
    @Autowired
    open fun episodeRepository(metadataExtractor: MetadataExtractor,
                               streamFileRepository: StreamFileRepository,
                               eventLog: EventLog): EpisodeRepository {
        return EpisodeRepository(streamFileRepository, metadataExtractor, eventLog)
    }

    @Bean
    @Autowired
    open fun infiniteEpisodeStream(episodeRepository: EpisodeRepository,
                                   eventLog: EventLog): InfiniteEpisodeStream {
        return InfiniteEpisodeStream(episodeRepository, eventLog)
    }
}
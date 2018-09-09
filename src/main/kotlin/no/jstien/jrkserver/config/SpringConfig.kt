package no.jstien.jrkserver.config

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import no.jstien.jrkserver.episodes.MetadataExtractor
import no.jstien.jrkserver.episodes.repo.EpisodeRepository
import no.jstien.jrkserver.episodes.repo.S3FileRepository
import no.jstien.jrkserver.stream.InfiniteEpisodeStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@PropertySource(value= ["classpath:config.properties"])
open class SpringConfig {
    @Value("\${s3.bucketname}")
    private val s3BucketName: String? = null

    @Bean
    open fun s3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.defaultClient()
    }

    @Bean
    open fun s3FileRepository(): S3FileRepository {
        return S3FileRepository(s3Client(), s3BucketName!!)
    }

    @Bean
    @Autowired
    open fun metadataExtractor(@Value("\${meta.seasonPrefix}") seasonPrefix: String): MetadataExtractor {
        return MetadataExtractor(seasonPrefix)
    }

    @Bean
    @Autowired
    open fun episodeRepository(metadataExtractor: MetadataExtractor): EpisodeRepository {
        return EpisodeRepository(s3FileRepository(), metadataExtractor)
    }

    @Bean
    @Autowired
    open fun infiniteEpisodeStream(episodeRepository: EpisodeRepository): InfiniteEpisodeStream {
        return InfiniteEpisodeStream(episodeRepository)
    }
}

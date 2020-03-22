package no.jstien.jrk

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import no.jstien.jrk.event.Event
import no.jstien.jrk.event.EventLog
import no.jstien.jrk.live.controller.TimeProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.*

@Configuration
@PropertySource(value= ["classpath:config.properties"])
@EnableAutoConfiguration
@ComponentScans(value = [
    // @ComponentScan(excludeFilters = [ ComponentScan.Filter(type = FilterType.REGEX, pattern = [ "no.jstien.jrk.live.*" ]) ])
    ComponentScan(basePackages = [
        "no.jstien.jrk.podcast",
        "no.jstien.jrk.live"
    ])
])
open class SpringConfig {
    @Value("\${s3.bucketname}") private val s3BucketName: String? = null

    @Bean
    open fun s3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.defaultClient()
    }

    @Bean
    open fun s3FileRepository(): S3FileRepository {
        return S3FileRepository(s3Client(), s3BucketName!!)
    }

    @Bean
    open fun eventLog(): EventLog {
        val eventLog = EventLog()
        eventLog.addEvent(Event.Type.SERVER_EVENT, "Server started")
        TimeProvider.eventLog = eventLog
        return eventLog
    }
}

package no.jstien.jrk.live

import no.jstien.jrk.event.EventLog
import no.jstien.jrk.live.controller.TimeProvider
import no.jstien.jrk.live.stream.EpisodeStream
import no.jstien.jrk.live.stream.InfiniteEpisodeStream
import no.jstien.jrk.util.ROOT_TEMP_DIRECTORY
import no.jstien.jrk.util.recursiveDelete
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.time.ZonedDateTime
import javax.annotation.PostConstruct

@Component
@ConditionalOnProperty(prefix = "live", name = ["enabled"])
internal class ScheduledMaintenanceTasks
    @Autowired constructor(private val infiniteEpisodeStream: InfiniteEpisodeStream, private val eventLog: EventLog)
{
    companion object {
        private val LOG = LogManager.getLogger()
    }

    @PostConstruct
    fun deleteStorageDirectory() {
        LOG.info("Deleting temp-root directory $ROOT_TEMP_DIRECTORY")
        recursiveDelete(File(ROOT_TEMP_DIRECTORY))
        File(ROOT_TEMP_DIRECTORY).mkdirs()

        infiniteEpisodeStream.setStartAvailability(TimeProvider.getTime())
    }

    @Scheduled(fixedRate = 30_000)
    fun triggerAvailabilityUpdate() {
        LOG.info("Triggering availability in infiniteEpisodeStream")
        infiniteEpisodeStream.updateEpisodeStreams(
                EpisodeStream.DEFAULT_AVAILABILITY_SEC,
                TimeProvider.getTime()
        )
    }

    @Scheduled(fixedRate = 3600_000)
    fun clearOldEventLogs() {
        val cutoff = ZonedDateTime.now().minusHours(24)
        eventLog.trimBefore(cutoff)
    }
}
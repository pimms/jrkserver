package no.jstien.jrkserver.config

import no.jstien.jrkserver.stream.InfiniteEpisodeStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class EpisodeStreamInitializer
    @Autowired constructor(private val infiniteEpisodeStream: InfiniteEpisodeStream)
{
    @EventListener(ApplicationReadyEvent::class)
    fun setStartAvailability() {
        infiniteEpisodeStream.setStartAvailability()
    }

}
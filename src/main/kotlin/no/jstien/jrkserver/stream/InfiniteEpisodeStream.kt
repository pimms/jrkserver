package no.jstien.jrkserver.stream

import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.episodes.EpisodeSegment
import no.jstien.jrkserver.episodes.repo.EpisodeRepository
import no.jstien.jrkserver.event.Event
import no.jstien.jrkserver.event.EventLog
import org.apache.logging.log4j.LogManager
import java.util.stream.Collectors

class InfiniteEpisodeStream(private val episodeRepository: EpisodeRepository,
                            private val eventLog: EventLog): EpisodeStream {
    companion object {
        private val LOG = LogManager.getLogger()
    }

    val currentEpisode: Episode?
        get() = episodeStreams.firstOrNull()?.episode

    private var episodeStreams = ArrayList<DefaultEpisodeStream>()


    override fun setStartAvailability(startTime: Double) {
        LOG.info("Setting start availability time at $startTime")

        // We kind of assume here that the 'startTime' is also the current time, which is not necessarily correct.
        // This is still not necessarily an incorrect call, but it's DEFINITELY a gray area.
        prepareStream(startTime, 0.0)
    }

    override fun getAvailableSegments(availabilitySecondsInterval: Double, currentTime: Double): List<EpisodeSegment> {
        removeExpiredEpisodes(currentTime)
        prepareStreamsIfNeeded(availabilitySecondsInterval, currentTime)

        return episodeStreams.stream()
                .flatMap { it.getAvailableSegments(availabilitySecondsInterval, currentTime).stream() }
                .collect(Collectors.toList())
    }


    private fun removeExpiredEpisodes(currentTime: Double) {
        while (episodeStreams.size != 0 && episodeStreams[0].getRemainingTime(currentTime) < 0.0) {
            LOG.info("Removing expired episode")
            eventLog.addEvent(Event("Episode evicted", "Episode ${episodeStreams.first().episode.displayName} has expired"))
            episodeStreams.first().cleanUp()
            episodeStreams.removeAt(0)
        }
    }

    private fun prepareStreamsIfNeeded(availability: Double, currentTime: Double) {
        if (episodeStreams.isEmpty()) {
            LOG.info("Preparing stream (no streams exist)")
            prepareStream(currentTime, 0.0)
        } else {
            var lastStream = episodeStreams.last()
            var remainingTime = lastStream.getRemainingTime(currentTime)
            while (remainingTime < availability) {
                LOG.info("Preparing stream (current last stream ends in $remainingTime seconds)")
                prepareStream(currentTime, remainingTime)
                lastStream = episodeStreams.last()
                remainingTime = lastStream.getRemainingTime(currentTime)
            }
        }
    }

    private fun prepareStream(currentTime: Double, delay: Double) {
        val episode = episodeRepository.getNextEpisode()
        val stream = DefaultEpisodeStream(episode)
        stream.setStartAvailability(currentTime + delay)
        episodeStreams.add(stream)

        eventLog.addEvent(Event("Episode prepared",
                                "Episode ${episode.displayName} (S ${episode.season}), starting in $delay seconds"))
    }
}
package no.jstien.roi.stream

import no.jstien.roi.episodes.Episode
import no.jstien.roi.episodes.EpisodeSegment
import no.jstien.roi.episodes.repo.EpisodeRepository
import no.jstien.roi.event.Event
import no.jstien.roi.event.EventLog
import org.apache.logging.log4j.LogManager
import java.time.ZonedDateTime
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
        updateEpisodeStreams(availabilitySecondsInterval, currentTime)

        return episodeStreams.stream()
                .flatMap { it.getAvailableSegments(availabilitySecondsInterval, currentTime).stream() }
                .collect(Collectors.toList())
    }

    fun updateEpisodeStreams(availabilitySecondsInterval: Double, currentTime: Double) {
        removeExpiredEpisodes(currentTime)
        prepareStreamsIfNeeded(availabilitySecondsInterval, currentTime)
    }


    private fun removeExpiredEpisodes(currentTime: Double) {
        while (episodeStreams.size != 0 && episodeStreams[0].getRemainingTime(currentTime) < 0.0) {
            LOG.info("Removing expired episode")

            eventLog.addEvent(Event.Type.SERVER_EVENT,
                              "Episode evicted",
                              "Episode ${episodeStreams.first().episode.displayName} has expired")

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

        logEpisodeStart(episode, delay);
    }

    private fun logEpisodeStart(episode: Episode, delay: Double) {
        val date = ZonedDateTime.now().plusSeconds(delay.toLong())

        eventLog.addEvent(Event.Type.SERVER_EVENT,
                "Episode prepared",
                "Episode '${episode.displayName}' starting  in $delay seconds")

        eventLog.addEvent(
            Event(
                Event.Type.EPISODE_PLAY,
                date,
                episode.displayName,
                episode.season
            )
        )
    }
}
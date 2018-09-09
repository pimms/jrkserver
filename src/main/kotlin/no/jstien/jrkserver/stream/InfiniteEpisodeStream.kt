package no.jstien.jrkserver.stream

import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.episodes.EpisodeRepository
import no.jstien.jrkserver.episodes.EpisodeSegment
import org.apache.logging.log4j.LogManager
import java.util.stream.Collectors

class InfiniteEpisodeStream(private val episodeRepository: EpisodeRepository): EpisodeStream {
    companion object {
        private val LOG = LogManager.getLogger()
    }

    val currentEpisode: Episode?
        get() = episodeStreams.firstOrNull()?.episode

    private var episodeStreams = ArrayList<DefaultEpisodeStream>()
    private var startTime = Double.MIN_VALUE


    override fun setStartAvailability(startTime: Double) {
        LOG.info("Setting start availability time at $startTime")
        this.startTime = startTime
        prepareStream(startTime)
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
            episodeStreams.first().cleanUp()
            episodeStreams.removeAt(0)
        }
    }

    private fun prepareStreamsIfNeeded(availability: Double, currentTime: Double) {
        if (episodeStreams.isEmpty()) {
            LOG.info("Preparing stream (no streams exist)")
            prepareStream(currentTime)
        } else {
            var lastStream = episodeStreams.last()
            var remainingTime = lastStream.getRemainingTime(currentTime)
            while (remainingTime < availability) {
                LOG.info("Preparing stream (current last stream ends in $remainingTime seconds)")
                prepareStream(currentTime + remainingTime)

                lastStream = episodeStreams.last()
                remainingTime = lastStream.getRemainingTime(currentTime)
            }
        }
    }

    private fun prepareStream(startTime: Double) {
        val episode = episodeRepository.getNextEpisode()
        val stream = DefaultEpisodeStream(episode)
        stream.setStartAvailability(startTime)
        episodeStreams.add(stream)
    }

    private fun verifyStartTimeDefined() {
        if (startTime == Double.MIN_VALUE) {
            throw RuntimeException("setStartAvailability must be called before getAvailableSegments")
        }
    }
}
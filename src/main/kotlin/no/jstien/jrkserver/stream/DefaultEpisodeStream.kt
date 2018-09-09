package no.jstien.jrkserver.stream

import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.episodes.EpisodeSegment
import no.jstien.jrkserver.stream.EpisodeStream.Companion.NANOS_PER_SEC

class DefaultEpisodeStream(episode: Episode): EpisodeStream {
    private val episode: Episode = episode
    private var startTimeNs = -1L

    override fun setStartAvailability(startTimeNs: Long) {
        this.startTimeNs = startTimeNs
    }

    override fun getAvailableSegments(availabilitySecondsInterval: Int, currentTimeNs: Long): List<EpisodeSegment> {
        verifyStartTimeDefined()

        val list = ArrayList<EpisodeSegment>()

        val startSec = startTimeNs.toDouble() / NANOS_PER_SEC.toDouble()
        val nowSec = currentTimeNs.toDouble() / NANOS_PER_SEC.toDouble()

        // Find the first segment to inlcude
        var time = startSec
        var index = 0
        while (index < episode.segmentCount && time + episode.segments[index].length < nowSec) {
            time += episode.segments[index].length
            index++
        }

        val endTime = nowSec + availabilitySecondsInterval.toDouble()
        while (time < endTime && index < episode.segmentCount) {
            list.add(episode.segments[index])
            index++
            time += episode.segments[index].length
        }

        return list
    }

    fun getRemainingTime(currentTimeNs: Long): Long {
        verifyStartTimeDefined()
        val episodeLengthNs = episode.length.toLong() * NANOS_PER_SEC
        return (startTimeNs + episodeLengthNs) - currentTimeNs
    }

    private fun verifyStartTimeDefined() {
        if (startTimeNs == -1L)
            throw RuntimeException("setStartAvailability must be called before getAvailableSegments")
    }

}
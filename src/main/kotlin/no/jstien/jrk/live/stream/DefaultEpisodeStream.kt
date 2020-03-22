package no.jstien.jrk.live.stream

import no.jstien.jrk.live.episodes.EpisodeSegment
import no.jstien.jrk.live.episodes.StreamableEpisode

class DefaultEpisodeStream(streamableEpisode: StreamableEpisode): EpisodeStream {
    val streamableEpisode: StreamableEpisode = streamableEpisode

    private var startTime = Double.MIN_VALUE


    fun getRemainingTime(currentTime: Double): Double {
        verifyStartTimeDefined()
        val episodeLength = streamableEpisode.length.toLong()
        return (startTime + episodeLength) - currentTime
    }

    fun cleanUp() {
        streamableEpisode.cleanUp()
    }

    override fun setStartAvailability(startTime: Double) {
        this.startTime = startTime
    }

    override fun getAvailableSegments(availabilitySecondsInterval: Double, currentTime: Double): List<EpisodeSegment> {
        verifyStartTimeDefined()

        val list = ArrayList<EpisodeSegment>()

        // Find the first segment to inlcude
        var time = startTime
        var index = 0
        while (index < streamableEpisode.segmentCount && time + streamableEpisode.segments[index].length < currentTime) {
            time += streamableEpisode.segments[index].length
            index++
        }

        val endTime = currentTime + availabilitySecondsInterval.toDouble()
        while (time < endTime && index < streamableEpisode.segmentCount) {
            list.add(streamableEpisode.segments[index])
            time += streamableEpisode.segments[index].length
            index++
        }

        return list
    }


    private fun verifyStartTimeDefined() {
        if (startTime == Double.MIN_VALUE) {
            throw RuntimeException("setStartAvailability must be called before getAvailableSegments")
        }
    }

}
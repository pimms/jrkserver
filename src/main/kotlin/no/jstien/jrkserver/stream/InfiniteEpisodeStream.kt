package no.jstien.jrkserver.stream

import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.episodes.EpisodeSegment
import no.jstien.jrkserver.episodes.S3Downloader

class InfiniteEpisodeStream(private val s3Downloader: S3Downloader): EpisodeStream {
    private var episodes = ArrayList<Episode>()
    private var startTimeNs: Long = -1L

    override fun setStartAvailability(startTime: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAvailableSegments(availabilitySecondsInterval: Double, currentTime: Double): List<EpisodeSegment> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
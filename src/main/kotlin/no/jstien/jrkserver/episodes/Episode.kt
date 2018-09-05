package no.jstien.jrkserver.episodes

import no.jstien.jrkserver.util.recursiveDelete
import org.apache.logging.log4j.LogManager
import java.io.File

class Episode(rootDirectory: String, segments: List<EpisodeSegment>) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    val segmentCount: Int
        get() = segments.size

    private val segments = segments
    private val rootDirectory = rootDirectory

    fun getSegment(index: Int): EpisodeSegment {
        return segments.get(index)
    }

    fun cleanUp() {
        LOGGER.info("Cleaning up segment directory $rootDirectory")
        segments.forEach { s -> s.close() }
        recursiveDelete(File(rootDirectory))
    }
}

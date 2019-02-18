package no.jstien.jrk.episodes

import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class EpisodeSegment(index: Int, length: Double, filePath: String): AutoCloseable {
    companion object {
        private val LOG = LogManager.getLogger()
        private var nextId = 0

        private fun getNextId(): Int {
            synchronized(nextId) {
                return nextId++
            }
        }
    }

    val id = getNextId()
    val index = index
    val length = length
    val filePath = filePath

    fun getStream(): InputStream {
        return FileInputStream(filePath)
    }

    override fun close() {
        try {
            File(filePath).delete()
        } catch (e: RuntimeException) {
            LOG.error("Failed to clean up EpisodeSegment file '$filePath'", e)
        }
    }

}

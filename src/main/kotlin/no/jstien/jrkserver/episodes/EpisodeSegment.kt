package no.jstien.jrkserver.episodes

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
    private var stream: InputStream? = null

    fun getStream(): InputStream {
        synchronized(this) {
            if (stream == null) {
                stream = FileInputStream(filePath)
            }
            return stream!!
        }
    }

    override fun close() {
        stream?.close()

        try {
            File(filePath).delete()
        } catch (e: RuntimeException) {
            LOG.error("Failed to clean up EpisodeSegment file '$filePath'", e)
        }
    }

}

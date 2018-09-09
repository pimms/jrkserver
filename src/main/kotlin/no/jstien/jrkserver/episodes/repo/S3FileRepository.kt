package no.jstien.jrkserver.episodes

import com.amazonaws.services.s3.AmazonS3
import no.jstien.jrkserver.util.ROOT_TEMP_DIRECTORY
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileOutputStream
import java.util.*

class S3FileRepository(s3Client: AmazonS3, s3BucketName: String) {
    companion object {
        private val LOGGER = LogManager.getLogger()
        private val TEMP_PATH = ROOT_TEMP_DIRECTORY + "/downloadedFromS3.mp3"
    }

    private val s3Client = s3Client
    private val s3BucketName = s3BucketName

    private val fileNames = ArrayList<String>()


    fun popRandomS3Key(): String {
        refreshEpisodesIfEmpty()
        val index = (0 until fileNames.size).random()
        val key = fileNames[index]
        fileNames.removeAt(index)
        return key
    }

    fun downloadFile(s3Key: String): String {
        try {
            LOGGER.info("Downloading S3-file '$s3Key'")
            val obj = s3Client.getObject(s3BucketName, s3Key)
            val inStream = obj.objectContent
            val outStream = FileOutputStream(File(TEMP_PATH))

            val buffer = ByteArray(1024)
            var readLen = 0

            readLen = inStream.read(buffer)
            while (readLen > 0) {
                outStream.write(buffer, 0, readLen)
                readLen = inStream.read(buffer)
            }

            inStream.close()
            outStream.close()

            LOGGER.info("Downloaded S3-file '$s3Key'")
            return TEMP_PATH
        } catch (e: Exception) {
            throw RuntimeException("Failed to download S3-file '$s3Key'", e)
        }
    }

    private  fun ClosedRange<Int>.random(): Int {
        val rand = Random()
        rand.setSeed(System.nanoTime())
        return rand.nextInt((endInclusive + 1) - start) +  start
    }

    private fun refreshEpisodesIfEmpty() {
        if (fileNames.isEmpty())
            refreshEpisodeNames()
    }

    private fun refreshEpisodeNames() {
        fileNames.clear()
        val result = s3Client.listObjectsV2(s3BucketName)
        result.objectSummaries.forEach { s -> fileNames.add(s.key) }
    }
}
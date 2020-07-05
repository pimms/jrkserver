package no.jstien.jrk

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsV2Request
import no.jstien.jrk.util.ROOT_TEMP_DIRECTORY
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class S3FileRepository(private val s3Client: AmazonS3, private val s3BucketName: String) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private val fileNames = ArrayList<String>()

    /**
     * Download a file to a temporary location on disk, and returns the path to the file.
     *
     * The tag parameter should be used to identify the context, as each tag/context will
     * use the same file path. This allows multiple contexts to work with downloaded files
     * simultaneously.
     */
    fun downloadFile(s3Key: String, tag: String = ""): String {
        try {
            LOGGER.info("Downloading S3-file '$s3Key'")

            val tempPath = "$ROOT_TEMP_DIRECTORY/downloaded$tag.mp3"

            val obj = s3Client.getObject(s3BucketName, s3Key)
            val inStream = obj.objectContent
            val outStream = FileOutputStream(File(tempPath))

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
            return tempPath
        } catch (e: Exception) {
            throw RuntimeException("Failed to download S3-file '$s3Key'", e)
        }
    }

    fun openStream(s3Key: String): InputStream {
        try {
            LOGGER.info("Opening stream to S3-file '$s3Key'")
            val obj = s3Client.getObject(s3BucketName, s3Key)
            val inStream = obj.objectContent
            return inStream
        } catch (e: Exception) {
            throw RuntimeException("Failed to open stream to S3-file '$s3Key'", e)
        }
    }

    fun getAllFileNames(): List<String> {
        refreshEpisodesIfEmpty()
        return fileNames
    }

    private fun refreshEpisodesIfEmpty() {
        if (fileNames.isEmpty())
            refreshEpisodeNames()
    }

    private fun refreshEpisodeNames() {
        val req = ListObjectsV2Request()
        req.bucketName = s3BucketName
        req.maxKeys = 10000
        fileNames.clear()

        var result = s3Client.listObjectsV2(req);
        while (result.objectSummaries.size > 0) {
            result.objectSummaries.forEach { s -> fileNames.add(s.key) }

            req.startAfter = fileNames.last()
            result = s3Client.listObjectsV2(req)
        }
    }
}

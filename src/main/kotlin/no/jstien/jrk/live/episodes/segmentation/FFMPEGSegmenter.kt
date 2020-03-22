package no.jstien.jrk.live.episodes.segmentation

import no.jstien.jrk.live.episodes.EpisodeSegment
import no.jstien.jrk.live.episodes.StreamableEpisode
import no.jstien.jrk.util.ProcessExecutor
import no.jstien.jrk.util.ROOT_TEMP_DIRECTORY
import org.apache.logging.log4j.LogManager
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import java.util.regex.Pattern

internal class FFMPEGSegmenter constructor(processExecutor: ProcessExecutor) {
    companion object {
        private val LOGGER = LogManager.getLogger()

        // Internal for testing purposes - never touch please
        internal var nextId: Int = 0
        private fun getSegmentationId(): String {
            synchronized(nextId) {
                return "segmentation_${nextId++}"
            }
        }
    }

    private val rootDirectory = ROOT_TEMP_DIRECTORY
    private val executor = processExecutor

    /* Defined for each call to segmentFile() */
    private var workDirectory: String = ""
    private var fileNameTemplate: String = ""

    init {
        verifyFfmpegInstallation()
    }

    private fun verifyFfmpegInstallation() {
        try {
            executor.execute("ffmpeg -version")

            // Expecting output on the form "ffmpeg version x.y.z copyright ..."
            // If we ever experience compatibility issues between versions, maybe
            // we should actually check the value as well... :-)
            val line = executor.stdout[0].split(" ")[2]
            if (executor.exitCode != 0)
                throw RuntimeException("ffmpeg invocation exited with status ${executor.exitCode}")
            if (!line.matches(Regex("([0-9]+\\.)*[0-9]")))
                throw RuntimeException("Expected version number, got $line")
            LOGGER.debug("FFMPEG version $line")
        } catch (e: RuntimeException) {
            throw RuntimeException("FFMPEG installation not found or not valid", e)
        }
    }

    fun segmentFile(request: SegmentationRequest): StreamableEpisode {
        LOGGER.info("Starting segmentation")
        generateRootPath()
        executeFfmpeg(request.filePath, request.desiredSegmentLength)

        val segments = parseFfmpegOutput()
        LOGGER.info("Segmentation completed")

        return StreamableEpisode(workDirectory, segments)
    }

    private fun generateRootPath() {
        val dirId = getSegmentationId()

        workDirectory = "${rootDirectory}/${dirId}"
        File(workDirectory).mkdirs()

        fileNameTemplate = "$workDirectory/seg"
    }

    private fun executeFfmpeg(filePath: String, desiredLength: Int) {
        val command = "ffmpeg -i $filePath -c:a aac -b:a 128k -vn -hls_time ${desiredLength} -hls_list_size 0 $fileNameTemplate.m3u8"
        executor.execute(command)

        if (executor.exitCode != 0)
            throw RuntimeException("FFMPEG exited with status code ${executor.exitCode}")
    }

    private fun parseFfmpegOutput(): List<EpisodeSegment> {
        LOGGER.info("Parsing FFMPEG output")

        val segmentList = ArrayList<EpisodeSegment>()
        val chunkListFile = fileNameTemplate + ".m3u8"

        val stream = FileReader(chunkListFile)
        val reader = BufferedReader(stream)

        var currentSegmentIndex = 0

        var line = reader.readLine()
        while (line != null) {
            if (line.startsWith("#EXTINF")) {
                val len = parseExtinfDuration(line)

                line = reader.readLine()
                val filePath = workDirectory + "/" + line
                if (!File(filePath).exists())
                    throw RuntimeException("Segment file '$filePath' does not actually exist")

                segmentList.add(EpisodeSegment(currentSegmentIndex++, len, filePath))
            }

            line = reader.readLine()
        }

        LOGGER.info("FFMPEG output parsed successfully")
        return segmentList
    }

    private fun parseExtinfDuration(line: String): Double {
        val pattern = Pattern.compile("#EXTINF:([0-9]+(\\.[0-9]+)?),?")
        val matcher = pattern.matcher(line)

        if (!matcher.matches())
            throw RuntimeException("Not a valid #EXTINF-line: '$line'")

        val match = matcher.group(1)
        val length = match.toDouble()

        if (length < 0 || length == 0.0)
            throw RuntimeException("Invalid EXTINF-length: '$match'")

        return length
    }

}

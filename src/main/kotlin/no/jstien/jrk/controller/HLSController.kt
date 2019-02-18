package no.jstien.jrk.controller

import no.jstien.jrk.episodes.StreamableEpisode.Companion.TARGET_SEGMENT_DURATION
import no.jstien.jrk.stream.InfiniteEpisodeStream
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/live")
class HLSController
    @Autowired constructor(
            private val infiniteEpisodeStream: InfiniteEpisodeStream
    )
{
    companion object {
        private val LOG = LogManager.getLogger()
    }

    @GetMapping("playlist.m3u8")
    fun getPlaylist(): ResponseEntity<String> {
        LOG.debug("GET: /playlist.m3u8")

        val body =
                """
                #EXTM3U
                #EXT-X-VERSION:3
                #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=40000
                chunks.m3u8
                """.trimIndent()
        return createM3u8Response(body)
    }

    @GetMapping("/chunks.m3u8")
    fun getChunkList(): ResponseEntity<String> {
        LOG.debug("GET: /chunks.m3u8")
        TimeProvider.registerActivity()

        val segments = infiniteEpisodeStream.getAvailableSegments(currentTime = TimeProvider.getTime())
        val firstInSequence = segments.stream().map { s -> s.id }.findFirst().orElse(0)

        val boilerplate =
                """
                #EXTM3U
                #EXT-X-VERSION:3
                #EXT-X-ALLOW-CACHE:NO
                #EXT-X-TARGETDURATION:$TARGET_SEGMENT_DURATION
                #EXT-X-MEDIA-SEQUENCE:${firstInSequence}

                """.trimIndent()
        val builder = StringBuilder()
        builder.append(boilerplate)

        segments.forEach { segment ->
            builder.append("#EXTINF:${segment.length}\n")
            builder.append("segment_${segment.id}.ts\n")
        }

        return createM3u8Response(builder.toString())
    }

    @GetMapping("/segment_{id}.ts")
    fun getSegment(@PathVariable id: Int): ResponseEntity<Any> {
        TimeProvider.registerActivity()

        val segment = infiniteEpisodeStream.getAvailableSegments(currentTime = TimeProvider.getTime())
                .find { s -> id == s.id }
                ?: return ResponseEntity.notFound().build()

        val stream = segment.getStream()
        val array = ByteArray(stream.available())
        stream.read(array)
        return ResponseEntity.ok().body(array)
    }

    private fun createM3u8Response(body: String): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/x-mpegURL")
        return ResponseEntity.ok().headers(headers).body(body)
    }

}
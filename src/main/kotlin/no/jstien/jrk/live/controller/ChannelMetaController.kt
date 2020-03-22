package no.jstien.jrk.live.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

private data class ChannelMeta(
    val streamName: String,

    val playlist: String,
    val streamPicture: String,
    val nowPlaying: String,
    val episodeLog: String,
    val eventLog: String
)

@RestController
@RequestMapping("/")
@ConditionalOnProperty(prefix = "live", name = ["enabled"])
class ChannelMetaController {
    @Value("\${channel.name}")
    private var channelName: String = "n/a"

    @GetMapping
    fun getChannelMeta(request: HttpServletRequest): ResponseEntity<Any> {
        val playlist = "live/playlist.m3u8"
        val streamPicture = "streamPicture"
        val nowPlaying = "live/nowPlaying"
        val episodeLog = "logs/episodes"
        val eventLog = "logs/events"

        val channelMeta = ChannelMeta(channelName, playlist, streamPicture, nowPlaying, episodeLog, eventLog)
        return ResponseEntity.ok(channelMeta)
    }

    @GetMapping("/streamPicture")
    fun getStreamPicture(): ResponseEntity<ByteArray> {
        val path = "stream-picture.png"
        val res = ClassPathResource(path)
        val bytes = res.inputStream.readBytes()

        val headers = HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return ResponseEntity(bytes, headers, HttpStatus.OK)
    }

}
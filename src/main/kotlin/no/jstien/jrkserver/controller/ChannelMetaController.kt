package no.jstien.jrkserver.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

private data class ChannelMeta(
        val playlistURL: String,
        val streamName: String
)

@RestController
@RequestMapping("/")
class ChannelMetaController {
    @Value("\${channel.name}")
    private var channelName: String = "n/a"

    @GetMapping
    fun getChannelMeta(request: HttpServletRequest): ResponseEntity<Any> {
        val url = request.requestURL.toString()
        val channelMeta = ChannelMeta(url + "live/playlist.m3u8", channelName)
        return ResponseEntity.ok(channelMeta)
    }

}
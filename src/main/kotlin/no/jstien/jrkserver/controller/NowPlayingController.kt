package no.jstien.jrkserver.controller

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.stream.InfiniteEpisodeStream
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/live")
class NowPlayingController
    @Autowired constructor(
            private val infiniteEpisodeStream: InfiniteEpisodeStream
    )
{
    companion object {
        private val LOG = LogManager.getLogger()
    }

    @GetMapping("currentinfo")
    fun getCurrentInfo(): ResponseEntity<String> {
        LOG.warn("Call to deprecated endpoint /live/currentinfo")
        return nowPlaying()
    }

    @GetMapping("nowPlaying")
    fun nowPlaying(): ResponseEntity<String> {
        try {
            val episode = infiniteEpisodeStream.currentEpisode
            return episode?.let { createNowPlayingResponse(it) }
                ?: createNothingPlayingEpisode()
        } catch (e: Exception) {
            val json = JsonObject()
            json.add("error", JsonPrimitive(e.message))
            return ResponseEntity.status(500).body(json.toString())
        }
    }

    fun createNowPlayingResponse(episode: Episode): ResponseEntity<String> {
        // As of August 16th, this is the sole reason we're including Gson. Not sure if it's worth it.
        val json = JsonObject()
        json.add("isPlaying", JsonPrimitive(true))
        json.add("name", JsonPrimitive("todo :)"))
        json.add("key", JsonPrimitive("todo :)"))
        json.add("season", JsonPrimitive("todo :)"))

        return ResponseEntity.ok(json.toString())
    }

    fun createNothingPlayingEpisode(): ResponseEntity<String> {
        val json = JsonObject()
        json.add("isPlaying", JsonPrimitive(false))
        return ResponseEntity.ok(json.toString())
    }
}
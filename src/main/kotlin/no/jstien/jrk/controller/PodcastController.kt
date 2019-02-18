package no.jstien.jrk.controller

import no.jstien.jrk.podcast.PodcastFeed
import no.jstien.jrk.podcast.PodcastRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/podcast")
class PodcastController(private val podcastRepository: PodcastRepository) {
    @GetMapping("/")
    fun getPodcastFeed(): ResponseEntity<PodcastFeed> {
        return ResponseEntity.ok(podcastRepository.getFeed())
    }
}

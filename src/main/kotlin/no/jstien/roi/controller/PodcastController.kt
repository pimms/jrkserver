package no.jstien.roi.controller

import no.jstien.roi.podcast.PodcastFeed
import no.jstien.roi.podcast.PodcastRepository
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

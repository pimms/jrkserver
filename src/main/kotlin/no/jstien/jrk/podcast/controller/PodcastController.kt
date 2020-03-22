package no.jstien.jrk.podcast.controller

import no.jstien.jrk.S3FileRepository
import no.jstien.jrk.podcast.PodcastRepository
import no.jstien.jrk.podcast.RssFeed
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/podcast")
class PodcastController(
    private val podcastRepository: PodcastRepository,
    private val s3FileRepository: S3FileRepository
) {
    @GetMapping(produces = [ MediaType.APPLICATION_XML_VALUE ])
    fun getPodcastFeed(): ResponseEntity<RssFeed> {
        val rssFeed = RssFeed(podcastRepository.getFeed())
        return ResponseEntity.ok(rssFeed)
    }

    @GetMapping("/episode/{s3Key}")
    fun getEpisode(@PathVariable s3Key: String): ResponseEntity<InputStreamResource> {
        var key = s3Key
        if (!key.endsWith(".mp3")) {
            key = "$key.mp3"
        }

        val stream = s3FileRepository.openStream(key)
        val streamResource = InputStreamResource(stream)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.setContentDispositionFormData("attachment", key)

        return ResponseEntity<InputStreamResource>(streamResource, headers, HttpStatus.OK)
    }
}

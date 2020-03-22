package no.jstien.jrk.podcast

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "rss")
class RssFeed(
    @JacksonXmlProperty(localName = "channel")
    val podcastFeed: PodcastFeed
) {
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:a10")
    val namespace: String = "http://www.w3.org/2005/Atom"

    @JacksonXmlProperty(isAttribute = true, localName = "version")
    val version: String = "2.0"
}

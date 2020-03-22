package no.jstien.jrk.podcast

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Enclosure(
    @JacksonXmlProperty(isAttribute = true)
    val url: String,

    @JacksonXmlProperty(isAttribute = true)
    val type: String = "audio/mpeg"
)

class PodcastItem(
    val title: String,
    val description: String,
    val pubDate: String,
    val enclosure: Enclosure
) {
    @JacksonXmlProperty(localName = "itunes:summary")
    val itunesSummary = description

    val guid = enclosure.url
}


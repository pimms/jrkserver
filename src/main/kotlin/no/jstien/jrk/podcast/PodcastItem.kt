package no.jstien.jrk.podcast

import javax.xml.bind.annotation.XmlAttribute

data class Enclosure(
    @XmlAttribute
    val url: String,

    @XmlAttribute
    val type: String = "audio/mpeg"
)

data class PodcastItem(
    val title: String,
    val description: String,
    val pubDate: String,
    val enclosure: Enclosure
)


package no.jstien.jrk.podcast

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class PodcastFeed(
    val title: String,
    val description: String,
    private val imageUrl: String,
    @JacksonXmlProperty(localName = "item")
    @JacksonXmlElementWrapper(useWrapping = false)
    val items: List<PodcastItem>
) {
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:itunes")
    val itunesNamespace = "http://www.itunes.com/dtds/podcast-1.0.dtd"

    val category: String = "Comedy"
    val language: String = "no"

    data class ItunesCategory(
        @JacksonXmlProperty(isAttribute = true)
        val category: String
    )
    @JacksonXmlProperty(localName = "itunes:category")
    val itunesCategory = ItunesCategory(category)

    data class ItunesImage(
        @JacksonXmlProperty(isAttribute = true)
        val href: String
    )
    @JacksonXmlProperty(localName = "itunes:image")
    val itunesImage = ItunesImage(imageUrl)

    @JacksonXmlProperty(localName = "itunes:summary")
    val itunesSummary = description

    data class Image(
        val title: String,
        val url: String,
        val link: String,
        val width: Int,
        val height: Int
    )
    val image = Image(title, imageUrl, imageUrl, 512, 512)
}
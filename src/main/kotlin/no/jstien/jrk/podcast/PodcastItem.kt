package no.jstien.jrk.podcast

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Enclosure(
    @JacksonXmlProperty(isAttribute = true)
    val url: String,

    @JacksonXmlProperty(isAttribute = true)
    val type: String = "audio/mpeg"
)

@JsonInclude(JsonInclude.Include.NON_NULL)
class PodcastItem(
    val title: String,
    val description: String,
    val pubDate: String,
    val enclosure: Enclosure,
    @JsonIgnore val duration: Int?
) {
    @JacksonXmlProperty(localName = "itunes:summary")
    val itunesSummary = description

    @JacksonXmlProperty(localName = "itunes:duration")
    val itunesDuration = stringDuration()

    val guid = enclosure.url

    private fun stringDuration(): String? {
        if (duration == null) {
            return null
        }

        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60

        var string = ""

        if (hours < 10) {
            string += "0$hours:"
        } else {
            string += "$hours:"
        }

        if (minutes < 10) {
            string += "0$minutes:"
        } else {
            string += "$minutes:"
        }

        if (seconds < 10) {
            string += "0$seconds"
        } else {
            string += "$seconds"
        }

        return string
    }
}


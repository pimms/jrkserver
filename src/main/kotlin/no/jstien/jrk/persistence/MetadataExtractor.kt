package no.jstien.jrk.persistence

import no.jstien.jrk.S3FileReference
import no.jstien.jrk.live.episodes.EpisodeMetadata
import java.text.SimpleDateFormat
import java.util.*

class MetadataExtractor(private val seasonPrefix: String?) {

    fun extractFromS3Reference(reference: S3FileReference): EpisodeMetadata {
        val s3Key = reference.key
        val season = extractSeason(s3Key)
        val date = extractDate(s3Key);
        val displayName = extractDisplayName(date)

        return EpisodeMetadata(displayName, date, season, s3Key, reference.size)
    }

    private fun extractSeason(s3Key: String): String {
        val season = s3Key.substring(0, 4)

        if (seasonPrefix.isNullOrBlank())
            return season

        return "$seasonPrefix $season"
    }

    private fun extractDate(s3Key: String): Date {
        if (s3Key.matches(Regex("[0-9]{8,}.*"))) {
            val format = SimpleDateFormat("yyyyMMdd")
            return format.parse(s3Key.substring(0, 8))
        } else {
            throw Exception("Failed to parse date from S3-key '$s3Key'")
        }
    }

    private fun extractDisplayName(date: Date): String {
        // can prolly be simplified by using proper locales
        val weekdays = arrayOf("Søndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag")
        val months = arrayOf("januar", "februar", "mars", "april", "mai", "juni", "juli", "august", "september", "oktober", "november", "desember")

        // deprecation schmeprecation
        val weekday = weekdays[date.day]
        val month = months[date.month]
        val day = date.date
        val year = date.year

        return "$weekday $day. $month"
    }
}
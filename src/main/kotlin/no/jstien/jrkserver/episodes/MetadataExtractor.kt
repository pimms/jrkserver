package no.jstien.jrkserver.episodes

import java.text.SimpleDateFormat

class MetadataExtractor {
    fun extractFromS3Key(s3Key: String): EpisodeMetadata {
        val season = extractSeason(s3Key)
        val displayName = extractDisplayName(s3Key)

        return EpisodeMetadata(displayName, season)
    }

    private fun extractSeason(s3Key: String): String {
        return s3Key.substring(0, 4)
    }

    private fun extractDisplayName(s3Key: String): String {
        // can prolly be simplified by using proper locales
        val weekdays = arrayOf("Søndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag")
        // val months = arrayOf("januar", "februar", "mars", "april", "mai", "juni", "juli", "august", "september", "oktober", "november", "desember")

        return if (s3Key.matches(Regex("[0-9]{8,}.*"))) {
            val format = SimpleDateFormat("yyyyMMdd")
            val date = format.parse(s3Key.substring(0, 8))

            // deprecation schmeprecation
            val weekday = weekdays[date.day]
            val month = date.month + 1
            val day = date.date

            "$weekday $day/$month"
        } else {
            s3Key
        }

    }
}
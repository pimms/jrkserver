package no.jstien.jrk.live.episodes

import java.time.Instant
import java.util.*

private const val NOT_AVAILABLE = "N/A"

class EpisodeMetadata {
    val displayName: String
    val description: String
    val date: Date
    val season: String
    val s3Key: String

    constructor(displayName: String, description: String, date: Date, season: String, s3Key: String) {
        this.displayName = displayName
        this.description = description
        this.date = date
        this.season = season
        this.s3Key = s3Key
    }

    constructor() {
        this.displayName = NOT_AVAILABLE
        this.description = NOT_AVAILABLE
        this.date = Date.from(Instant.ofEpochMilli(0))
        this.season = NOT_AVAILABLE
        this.s3Key = NOT_AVAILABLE
    }
}
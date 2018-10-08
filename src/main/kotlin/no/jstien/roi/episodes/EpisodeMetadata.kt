package no.jstien.roi.episodes

private const val NOT_AVAILABLE = "N/A"

class EpisodeMetadata {
    var displayName: String
    var season: String
    var s3Key: String

    constructor(displayName: String, season: String, s3Key: String) {
        this.displayName = displayName
        this.season = season
        this.s3Key = s3Key
    }

    constructor() {
        this.displayName = NOT_AVAILABLE
        this.season = NOT_AVAILABLE
        this.s3Key = NOT_AVAILABLE
    }
}
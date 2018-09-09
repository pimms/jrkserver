package no.jstien.jrkserver.episodes

private const val NOT_AVAILABLE = "N/A"

class EpisodeMetadata {
    var displayName: String
    var season: String

    constructor(displayName: String, season: String) {
        this.displayName = displayName
        this.season = season
    }

    constructor() {
        this.displayName = NOT_AVAILABLE
        this.season = NOT_AVAILABLE
    }
}
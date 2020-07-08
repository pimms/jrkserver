package no.jstien.jrk.podcast

data class PodcastManifest(
    val title: String,
    val description: String,
    val rootUrl: String,
    val imageUrl: String
) {
    val link = "$rootUrl/podcast"
}

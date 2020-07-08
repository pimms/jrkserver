package no.jstien.jrk.persistence

data class PersistentEpisode(
        val s3Key: String,
        val duration: Int?,
        val desc: String?
)

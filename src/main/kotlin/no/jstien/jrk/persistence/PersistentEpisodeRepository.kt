package no.jstien.jrk.persistence

import org.apache.logging.log4j.LogManager
import org.jdeferred.DoneCallback
import org.restonfire.FirebaseRestDatabase

class PersistentEpisodeRepository(
    private val database: FirebaseRestDatabase
) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    fun saveEpisode(episode: PersistentEpisode) {
        val pathName = episode.s3Key.replace('.', '-')

        database.getReference("rr/episodes/${pathName}")
                .setValue(episode)
                .then { episode -> LOGGER.info("Saved episode $episode") }
                .fail { err -> LOGGER.error("Failed to save episode: $err") }
    }

    fun getAllEpisodes(handler: (List<PersistentEpisode>) -> Unit) {
        database.getReference("rr/episodes/")
                .query()
                .orderByKey()
                .run(Map::class.java)
                .done(DoneCallback { result ->
                    if (result == null) {
                        handler(emptyList())
                        return@DoneCallback
                    }

                    val episodes = result.values.map { v ->
                        if (v is Map<*, *>) {
                            var duration: Int?

                            if (v.contains("s3Key") && v["s3Key"] is String) {
                                val s3Key = v["s3Key"] as String
                                var duration: Int? = null

                                if (v.containsKey("duration") && v["duration"] is Double) {
                                    duration = (v["duration"] as Double).toInt()
                                }

                                return@map PersistentEpisode(s3Key, duration)
                            }

                            return@map null
                        }

                        return@map null
                    }.filter { v -> v != null }.map { v -> v!! }

                    handler(episodes)
                })
    }
}
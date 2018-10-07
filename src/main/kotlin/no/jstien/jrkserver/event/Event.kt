package no.jstien.jrkserver.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

data class Event(
    @JsonIgnore val type: Type,
    val timestamp: ZonedDateTime,
    val title: String,
    val description: String?
) {
    enum class Type {
        SERVER_EVENT,
        EPISODE_PLAY
    }

    constructor(type: Type, title: String) : this(type, ZonedDateTime.now(), title, null)
    constructor(type: Type, title: String, desc: String) : this(type, ZonedDateTime.now(), title, desc)
}
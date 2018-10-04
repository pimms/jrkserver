package no.jstien.jrkserver.event

import java.time.ZonedDateTime

data class Event(
    val timestamp: ZonedDateTime,
    val title: String,
    val description: String?
) {
    constructor(title: String) : this(ZonedDateTime.now(), title, null)
    constructor(title: String, desc: String) : this(ZonedDateTime.now(), title, desc)
}
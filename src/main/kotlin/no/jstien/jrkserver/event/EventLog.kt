package no.jstien.jrkserver.event

import java.time.ZonedDateTime

class EventLog {
    private val events: ArrayList<Event> = ArrayList()

    fun getEvents(): List<Event> {
        return events.toList()
    }

    fun addEvent(event: Event) {
        // Heuristric optimization: run through the list in reverse order, as we can safely
        // expect the input to *almost always* be later than the last element in the existing list.
        var index = events.size
        while (index > 0 && events[index-1].timestamp.isAfter(event.timestamp)) {
            index--
        }

        events.add(index, event)
    }

    fun trimBefore(time: ZonedDateTime) {
        events.removeIf { e -> e.timestamp.isBefore(time) }
    }
}
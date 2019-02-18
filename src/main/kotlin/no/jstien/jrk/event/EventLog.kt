package no.jstien.jrk.event

import org.apache.logging.log4j.LogManager
import java.time.ZonedDateTime

class EventLog {
    companion object {
        private val LOG = LogManager.getLogger()
    }

    private val events: ArrayList<Event> = ArrayList()


    fun getEvents(): List<Event> {
        return events.toList()
    }

    fun getEvents(filter: Event.Type): List<Event> {
        return events.filter { e -> e.type == filter }.toList()
    }


    fun addEvent(type: Event.Type, title: String) {
        addEvent(Event(type, title))
    }

    fun addEvent(type: Event.Type, title: String, description: String) {
        addEvent(Event(type, title, description))
    }

    fun addEvent(event: Event) {
        // Heuristric optimization: run through the list in reverse order, as we can safely
        // expect the input to *almost always* be later than the last element in the existing list.
        var index = events.size
        while (index > 0 && events[index-1].timestamp.isAfter(event.timestamp)) {
            index--
        }

        events.add(index, event)

        if (event.description != null) {
            LOG.info("[${event.type}] ${event.title}: ${event.description}")
        } else {
            LOG.info("[${event.type}] ${event.title}")
        }
    }


    fun trimBefore(time: ZonedDateTime) {
        events.removeIf { e -> e.timestamp.isBefore(time) }
    }
}
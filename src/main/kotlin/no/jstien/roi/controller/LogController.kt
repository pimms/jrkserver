package no.jstien.roi.controller

import no.jstien.roi.event.Event
import no.jstien.roi.event.EventLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/logs")
class LogController
    @Autowired constructor(
        private val eventLog: EventLog
    )
{
    @GetMapping("events")
    fun getServerEvents(): ResponseEntity<List<Event>> {
        val events = eventLog.getEvents(Event.Type.SERVER_EVENT)
        return ResponseEntity.ok(events)
    }

    @GetMapping("episodes")
    fun getEpisodeHistory(): ResponseEntity<List<Event>> {
        val events = eventLog.getEvents(Event.Type.EPISODE_PLAY)
        return ResponseEntity.ok(events)
    }
}
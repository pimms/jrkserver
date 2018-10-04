package no.jstien.jrkserver.controller

import no.jstien.jrkserver.event.Event
import no.jstien.jrkserver.event.EventLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/live")
class LogController
    @Autowired constructor(
        private val eventLog: EventLog
    )
{
    @GetMapping("eventlog")
    fun getEvents():ResponseEntity<List<Event>> {
        return ResponseEntity.ok(eventLog.getEvents())
    }
}
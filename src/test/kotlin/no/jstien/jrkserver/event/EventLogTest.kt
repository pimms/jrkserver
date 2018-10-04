package no.jstien.jrkserver.event

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

internal class EventLogTest {
    @Test
    fun `events are insertion-sorted`() {
        val log = EventLog()

        val event1 = Event(ZonedDateTime.of(2010, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")), "E1", "E1")
        val event2 = Event(ZonedDateTime.of(2011, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")), "E2", "E2")
        val event3 = Event(ZonedDateTime.of(2012, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")), "E3", "E3")

        log.addEvent(event1)
        log.addEvent(event3)
        log.addEvent(event2)

        log.getEvents() shouldBe listOf(event1, event2, event3)
    }


    @Test
    fun `trimming removes events before a prior date`() {
        val log = EventLog()

        val event1 = Event(ZonedDateTime.of(2010, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")), "E1", "E1")
        val cutoff = ZonedDateTime.of(2011, 1, 1, 1, 0, 0, 0, ZoneId.of("UTC"))
        val event2 = Event(ZonedDateTime.of(2011, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")), "E2", "E2")
        val event3 = Event(ZonedDateTime.of(2012, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC")), "E3", "E3")

        log.addEvent(event1)
        log.addEvent(event2)
        log.addEvent(event3)

        log.getEvents() shouldBe listOf(event1, event2, event3)
        log.trimBefore(cutoff)
        log.getEvents() shouldBe listOf(event2, event3)
    }
}
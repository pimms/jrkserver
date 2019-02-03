package no.jstien.roi.controller

import no.jstien.roi.event.Event
import no.jstien.roi.event.EventLog
import org.springframework.beans.factory.annotation.Autowired

object TimeProvider {
    private const val IDLE_THRESHOLD_SEC: Double = 5400.0
    private const val NANOS_PER_SEC = 1_000_000_000L

    @Autowired
    private var eventLog: EventLog? = null

    private var lastActivity: Double = getClockTime()
    private var paused: Boolean = false

    private var timeOffset: Double = 0.0
    private var pauseStartTime: Double = 0.0

    fun getTime(): Double {
        val clockTime = getClockTime()

        if (!paused && clockTime - lastActivity >= IDLE_THRESHOLD_SEC) {
            eventLog?.addEvent(Event.Type.SERVER_EVENT, "Pausing S3 fetch loop due to inactivity")
            paused = true
            pauseStartTime = clockTime
        }

        if (paused) {
            return pauseStartTime
        }

        return adjustTime(clockTime)
    }

    fun registerActivity() {
        lastActivity = getClockTime()

        if (paused) {
            eventLog?.addEvent(Event.Type.SERVER_EVENT, "Resuming S3 fetch loop due to inactivity")
            paused = false
            timeOffset += (lastActivity - pauseStartTime)
        }
    }

    private fun getClockTime(): Double {
        return System.nanoTime().toDouble() / NANOS_PER_SEC.toDouble()
    }

    private fun adjustTime(clockTime: Double): Double {
        return clockTime - timeOffset
    }
}
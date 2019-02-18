package no.jstien.jrk.controller

import no.jstien.jrk.event.Event
import no.jstien.jrk.event.EventLog

object TimeProvider {
    private const val IDLE_THRESHOLD_SEC: Double = 120.0
    private const val NANOS_PER_SEC = 1_000_000_000L

    var eventLog: EventLog? = null

    private var lastActivity: Double = getClockTime()
    private var paused: Boolean = false

    private var timeOffset: Double = getClockTime()
    private var pauseStartTime: Double = 0.0

    fun getTime(): Double {
        val clockTime = getClockTime()

        if (!paused && clockTime - lastActivity >= IDLE_THRESHOLD_SEC) {
            eventLog?.addEvent(Event.Type.SERVER_EVENT, "Pausing S3 fetch loop due to inactivity", "deltaTime=${adjustTime(clockTime)}, idleTime=${clockTime - lastActivity}, timeOffset=$timeOffset")
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
            paused = false
            timeOffset += (lastActivity - pauseStartTime)
            eventLog?.addEvent(Event.Type.SERVER_EVENT, "Resuming S3 fetch loop", "deltaTime=${adjustTime(lastActivity)}, timeOffset=$timeOffset")
        }
    }

    private fun getClockTime(): Double {
        return System.nanoTime().toDouble() / NANOS_PER_SEC.toDouble()
    }

    private fun adjustTime(clockTime: Double): Double {
        return clockTime - timeOffset
    }
}
package no.jstien.jrk.stream

import no.jstien.jrk.episodes.EpisodeSegment

interface EpisodeStream {
    companion object {
        private const val NANOS_PER_SEC = 1_000_000_000L
        const val DEFAULT_AVAILABILITY_SEC = 120.0

        fun defaultTimeProvider(): Double {
            return System.nanoTime().toDouble() / NANOS_PER_SEC.toDouble()
        }
    }

    /**
     * Flag the start time of the segment stream at @startTime. This point in time could be
     * in the past, present, or future.
     *
     * Must be called before getRemainingTime and getAvailableSegments
     */
    fun setStartAvailability(startTime: Double = defaultTimeProvider())

    /**
     * Retrieve the list of available segments at the given point in time. If the EpisodeStream's
     * start time is more than @availabilityInterval seconds in the future, an empty list will always
     * be returned. If the start time is in the future but less than @availabilityInterval seconds away,
     * up to (availability iterval - start time - now) seconds will be returned.
     *
     * Throws RuntimeException if @setStartAvailability has not yet been called.
     */
    fun getAvailableSegments(
            availabilitySecondsInterval: Double = DEFAULT_AVAILABILITY_SEC,
            currentTime:  Double = defaultTimeProvider()
    ): List<EpisodeSegment>
}
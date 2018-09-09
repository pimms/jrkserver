package no.jstien.jrkserver.stream

import no.jstien.jrkserver.episodes.EpisodeSegment

interface EpisodeStream {
    companion object {
        const val NANOS_PER_SEC = 1_000_000_000L
        private const val DEFAULT_AVAILABILITY_SEC = 60

        fun defaultTimeProvider(): Long {
            return System.nanoTime()
        }
    }

    /**
     * Flag the start time of the segment stream at @startTimeNs. This point in time could be
     * in the past, present, or future.
     *
     * Must be called before getRemainingTime and getAvailableSegments
     */
    fun setStartAvailability(startTimeNs: Long = defaultTimeProvider())

    /**
     * Retrieve the list of available segments at the given point in time. If the EpisodeStream's
     * start time is more than @availabilityInterval seconds in the future, an empty list will always
     * be returned. If the start time is in the future but less than @availabilityInterval seconds away,
     * up to (availability iterval - start time - now) seconds will be returned.
     *
     * Throws RuntimeException if @setStartAvailability has not yet been called.
     */
    fun getAvailableSegments(
            availabilitySecondsInterval: Int = DEFAULT_AVAILABILITY_SEC,
            currentTimeNs:  Long = defaultTimeProvider()
    ): List<EpisodeSegment>
}
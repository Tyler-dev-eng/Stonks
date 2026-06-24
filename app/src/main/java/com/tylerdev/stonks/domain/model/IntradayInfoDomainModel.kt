package com.tylerdev.stonks.domain.model

import java.time.LocalDateTime

/**
 * Domain representation of a single intraday price observation.
 *
 * Decouples presentation and charting logic from data-layer types. Repositories map remote
 * intraday time-series rows into this model; ViewModels and UI consume it without depending on
 * Moshi DTOs or raw timestamp strings.
 */
data class IntradayInfoDomainModel(
    /** Date and time of the price observation. */
    val date: LocalDateTime,
    /** Closing price at [date] for the intraday interval. */
    val close: Double
)

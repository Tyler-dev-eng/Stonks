package com.tylerdev.stonks.data.remote.dto

/**
 * DTO for a single row of Alpha Vantage intraday time-series data.
 *
 * Holds the raw timestamp string and closing price parsed from the intraday endpoint. Repositories
 * map this type into [IntradayInfoDomainModel], converting [timestamp] to a typed date-time value.
 */
data class IntradayInfoDto(
    /** Observation timestamp as returned by the API (e.g. `2024-01-15 16:00:00`). */
    val timestamp: String,
    /** Closing price for the interval at [timestamp]. */
    val close: Double
)

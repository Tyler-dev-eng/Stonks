package com.tylerdev.stonks.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a company listed on a public exchange.
 *
 * Persists cached listing data fetched from the Alpha Vantage service. Repositories map remote
 * CSV rows into this entity for local storage and convert stored records into domain models for
 * the rest of the app.
 */
@Entity
data class CompanyListingEntity(
    /** Display name of the listed company. */
    val name: String,
    /** Ticker symbol used to identify the security (e.g. AAPL). */
    val symbol: String,
    /** Exchange on which the company is listed (e.g. NASDAQ). */
    val exchange: String,
    /** Auto-generated primary key; null when inserting a new row. */
    @PrimaryKey val id: Int? = null
)
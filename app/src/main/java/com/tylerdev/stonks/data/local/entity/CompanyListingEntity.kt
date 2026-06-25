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
    val name: String,
    val symbol: String,
    val exchange: String,
    val isFavorite: Boolean = false,
    @PrimaryKey val id: Int? = null
)
package com.tylerdev.stonks.domain.model

/**
 * Domain representation of a company listed on a public exchange.
 *
 * Decouples presentation and use-case logic from data-layer types. Repositories map remote and
 * cached sources into this model; ViewModels and UI consume it without depending on Room or
 * network DTOs.
 */
data class CompanyListingDomainModel(
    val name: String,
    val symbol: String,
    val exchange: String,
    val isFavorite: Boolean = false
)

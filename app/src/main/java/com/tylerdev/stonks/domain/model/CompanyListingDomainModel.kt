package com.tylerdev.stonks.domain.model

/**
 * Domain representation of a company listed on a public exchange.
 *
 * Decouples presentation and use-case logic from data-layer types. Repositories map remote and
 * cached sources into this model; ViewModels and UI consume it without depending on Room or
 * network DTOs.
 */
data class CompanyListingDomainModel(
    /** Display name of the listed company. */
    val name: String,
    /** Ticker symbol used to identify the security (e.g. AAPL). */
    val symbol: String,
    /** Exchange on which the company is listed (e.g. NASDAQ). */
    val exchange: String
)

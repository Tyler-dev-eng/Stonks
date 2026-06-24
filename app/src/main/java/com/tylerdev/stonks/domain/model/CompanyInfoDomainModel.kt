package com.tylerdev.stonks.domain.model

/**
 * Domain representation of detailed company profile data.
 *
 * Decouples presentation and use-case logic from data-layer types. Repositories map remote
 * overview responses into this model; ViewModels and UI consume it without depending on Moshi
 * DTOs or Retrofit types.
 */
data class CompanyInfoDomainModel(
    /** Ticker symbol identifying the company (e.g. AAPL). */
    val symbol: String,
    /** Long-form business description of the company. */
    val description: String,
    /** Display name of the company. */
    val name: String,
    /** Country where the company is headquartered. */
    val country: String,
    /** Primary industry sector of the company. */
    val industry: String
)

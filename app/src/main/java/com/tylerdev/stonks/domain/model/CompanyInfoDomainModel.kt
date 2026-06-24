package com.tylerdev.stonks.domain.model

/**
 * Domain representation of detailed company profile data.
 *
 * Decouples presentation and use-case logic from data-layer types. Repositories map remote
 * overview responses into this model; ViewModels and UI consume it without depending on Moshi
 * DTOs or Retrofit types.
 */
data class CompanyInfoDomainModel(
    val symbol: String,
    val description: String,
    val name: String,
    val country: String,
    val industry: String,
    val logoUrl: String
)

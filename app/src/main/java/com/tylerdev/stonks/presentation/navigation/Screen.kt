package com.tylerdev.stonks.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CompanyListings

@Serializable
data class CompanyDetail(val symbol: String)

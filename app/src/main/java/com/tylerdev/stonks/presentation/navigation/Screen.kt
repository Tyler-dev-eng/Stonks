package com.tylerdev.stonks.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for the app.
 *
 * Each route is a [Serializable] type consumed by [NavGraph] and Jetpack Navigation Compose
 * (`composable<T>`, `toRoute()`). Use these types as [androidx.navigation.compose.NavHost]
 * destinations instead of string routes.
 */

/** Route for the searchable list of company listings; also the app's start destination. */
@Serializable
data object CompanyListings

/**
 * Route for a single company's detail screen.
 *
 * @property symbol Ticker symbol identifying the company to display (e.g. AAPL).
 */
@Serializable
data class CompanyInfo(val symbol: String)

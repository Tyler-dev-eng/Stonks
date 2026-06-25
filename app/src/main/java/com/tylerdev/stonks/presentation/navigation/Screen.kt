package com.tylerdev.stonks.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for the app.
 *
 * Each route is a [Serializable] type consumed by [NavGraph] and Jetpack Navigation Compose
 * (`composable<T>`, `toRoute()`). Use these types as [androidx.navigation.compose.NavHost]
 * destinations instead of string routes.
 */

@Serializable
data object CompanyListings

@Serializable
data class CompanyInfo(val symbol: String)

@Serializable
data object MarketNews

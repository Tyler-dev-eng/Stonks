package com.tylerdev.stonks.presentation.company_listings

import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.model.StockQuoteDomainModel

/**
 * UI state for the company listings screen.
 *
 * Exposed by [CompanyListingsViewModel] and collected by Compose UI to render the listing list,
 * loading indicators, pull-to-refresh state, and the active search query.
 */
data class CompanyListingsState(
    val companies: List<CompanyListingDomainModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val selectedTab: ListingsTab = ListingsTab.ALL,
    val quotesBySymbol: Map<String, StockQuoteDomainModel> = emptyMap()
) {
    val displayedCompanies: List<CompanyListingDomainModel>
        get() = if (selectedTab == ListingsTab.FAVORITES) companies.filter { it.isFavorite } else companies
}

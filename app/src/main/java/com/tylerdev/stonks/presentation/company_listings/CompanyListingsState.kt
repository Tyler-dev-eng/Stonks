package com.tylerdev.stonks.presentation.company_listings

import com.tylerdev.stonks.domain.model.CompanyListingDomainModel

/**
 * UI state for the company listings screen.
 *
 * Exposed by [CompanyListingsViewModel] and collected by Compose UI to render the listing list,
 * loading indicators, pull-to-refresh state, and the active search query.
 */
data class CompanyListingsState(
    /** Company listings currently shown in the list. */
    val companies: List<CompanyListingDomainModel> = emptyList(),
    /** True while an initial load or search is in progress. */
    val isLoading: Boolean = false,
    /** True while a pull-to-refresh or background refresh is in progress. */
    val isRefreshing: Boolean = false,
    /** Current search input used to filter listings. */
    val searchQuery: String = "",
    /** Non-null while a Snackbar error should be displayed; null after it is dismissed. */
    val errorMessage: String? = null
)

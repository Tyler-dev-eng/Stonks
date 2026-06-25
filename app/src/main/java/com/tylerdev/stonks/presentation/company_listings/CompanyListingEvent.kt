package com.tylerdev.stonks.presentation.company_listings

/**
 * User and lifecycle events handled by [CompanyListingsViewModel].
 *
 * The UI dispatches these to trigger data refresh or update the search filter.
 */
sealed class CompanyListingEvent {

    /** Requests a remote refresh of company listing data. */
    object Refresh : CompanyListingEvent()

    /**
     * Fired when the user changes the search field.
     *
     * @property query Updated search text.
     */
    data class OnSearchQueryChange(val query: String) : CompanyListingEvent()

    /** Fired when the error Snackbar is dismissed. */
    object ErrorDismissed : CompanyListingEvent()
}
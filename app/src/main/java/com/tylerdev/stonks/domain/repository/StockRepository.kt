package com.tylerdev.stonks.domain.repository

import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Contract for accessing company listing data.
 *
 * Defined in the domain layer so use cases depend on abstractions rather than data-layer
 * implementations. Concrete implementations coordinate remote API calls, local caching, and
 * mapping into [CompanyListingDomainModel].
 */
interface StockRepository {

    /**
     * Loads company listings, optionally refreshing from the remote service.
     *
     * Emits [Resource] states as data is fetched from the network and/or local cache, then filtered
     * by the supplied search term.
     *
     * @param fetchFromRemote When true, fetches fresh listing data from the remote service before
     *   querying the cache; when false, reads from local storage only.
     * @param query Search term used to filter listings by name or ticker symbol.
     * @return Flow of loading, success, and error states wrapping matching listings.
     */
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingDomainModel>>>
}
package com.tylerdev.stonks.domain.repository

import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Contract for accessing stock market data.
 *
 * Defined in the domain layer so use cases depend on abstractions rather than data-layer
 * implementations. Concrete implementations coordinate remote API calls, local caching where
 * applicable, and mapping into domain models.
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

    /**
     * Loads hourly intraday price observations for a company.
     *
     * @param symbol Ticker symbol to query (e.g. AAPL).
     * @return [Resource.Success] with parsed intraday rows, or [Resource.Error] on failure.
     */
    suspend fun getIntradayInfo(
        symbol: String
    ): Resource<List<IntradayInfoDomainModel>>

    /**
     * Loads company profile and overview data for a symbol.
     *
     * @param symbol Ticker symbol to query (e.g. AAPL).
     * @return [Resource.Success] with overview fields, or [Resource.Error] on failure.
     */
    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfoDomainModel>
}
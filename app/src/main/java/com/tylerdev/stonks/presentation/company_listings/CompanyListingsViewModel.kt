package com.tylerdev.stonks.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel for the company listings screen.
 *
 * Exposes [state] for Compose and routes [CompanyListingEvent] actions to [StockRepository].
 * Loads cached listings on creation, debounces search input by 500 ms before re-querying, and
 * requests a remote refresh when [CompanyListingEvent.Refresh] is received.
 */
@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    /** Current screen state observed by the listings UI. */
    var state by mutableStateOf(CompanyListingsState())

    /** In-flight debounced search job; cancelled when the query changes again. */
    private var searchJob: Job? = null

    init {
        getCompanyListings()
    }

    /**
     * Handles user actions from the listings screen.
     *
     * Refresh triggers a remote reload. Search updates [CompanyListingsState.searchQuery]
     * immediately and fetches matching listings after the debounce delay.
     *
     * @param event Refresh or search-query change dispatched by the UI.
     */
    fun onEvent(event: CompanyListingEvent) {
        when(event) {
            is CompanyListingEvent.Refresh -> {
                getCompanyListings(fetchFromRemote = true)
            }
            is CompanyListingEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L.milliseconds)
                    getCompanyListings()
                }
            }
        }
    }

    /**
     * Loads listings from the repository and updates [state] from emitted [Resource] values.
     *
     * [Resource.Success] updates [CompanyListingsState.companies]; [Resource.Loading] toggles
     * [CompanyListingsState.isLoading]. Errors are ignored and any listings already in [state]
     * remain visible.
     *
     * @param fetchFromRemote When true, forces a remote refresh before returning results.
     * @param query Lowercased search term passed to the repository; defaults to [CompanyListingsState.searchQuery].
     */
    private fun getCompanyListings(
        fetchFromRemote: Boolean = false,
        query: String = state.searchQuery.lowercase()
    ) {
        viewModelScope.launch {
            repository
                .getCompanyListings(fetchFromRemote, query)
                .collect { result ->
                   when(result) {
                       is Resource.Success -> {
                           result.data?.let { listings ->
                               state = state.copy(
                                   companies = listings
                               )
                           }
                       }
                       is Resource.Error -> Unit
                       is Resource.Loading -> {
                           state = state.copy(isLoading = result.isLoading)
                       }
                   }
                }
        }
    }
}
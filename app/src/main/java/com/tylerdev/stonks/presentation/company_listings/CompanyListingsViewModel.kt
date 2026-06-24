package com.tylerdev.stonks.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerdev.stonks.domain.usecase.GetCompanyListingsUseCase
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
    private val getCompanyListings: GetCompanyListingsUseCase
) : ViewModel() {

    /** Current screen state observed by the listings UI. */
    var state by mutableStateOf(CompanyListingsState())

    /** In-flight debounced search job; cancelled when the query changes again. */
    private var searchJob: Job? = null

    init {
        loadCompanyListings()
    }

    fun onEvent(event: CompanyListingEvent) {
        when(event) {
            is CompanyListingEvent.Refresh -> {
                loadCompanyListings(fetchFromRemote = true)
            }
            is CompanyListingEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L.milliseconds)
                    loadCompanyListings()
                }
            }
        }
    }

    private fun loadCompanyListings(
        fetchFromRemote: Boolean = false,
        query: String = state.searchQuery.lowercase()
    ) {
        viewModelScope.launch {
            getCompanyListings(fetchFromRemote, query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                state = state.copy(companies = listings)
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
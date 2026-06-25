package com.tylerdev.stonks.presentation.company_listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerdev.stonks.domain.usecase.GetCompanyListingsUseCase
import com.tylerdev.stonks.domain.usecase.GetStockQuoteUseCase
import com.tylerdev.stonks.domain.usecase.ToggleFavoriteUseCase
import com.tylerdev.stonks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val getCompanyListings: GetCompanyListingsUseCase,
    private val getStockQuote: GetStockQuoteUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CompanyListingsState())
    val state: StateFlow<CompanyListingsState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCompanyListings()
    }

    fun onEvent(event: CompanyListingEvent) {
        when (event) {
            is CompanyListingEvent.Refresh -> loadCompanyListings(fetchFromRemote = true)
            is CompanyListingEvent.ErrorDismissed -> _state.update { it.copy(errorMessage = null) }
            is CompanyListingEvent.SelectTab -> {
                _state.update { it.copy(selectedTab = event.tab) }
                if (event.tab == ListingsTab.FAVORITES) loadWatchlistQuotes()
            }
            is CompanyListingEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavorite(event.symbol, event.isFavorite)
                    _state.update { state ->
                        state.copy(
                            companies = state.companies.map { company ->
                                if (company.symbol == event.symbol) company.copy(isFavorite = event.isFavorite)
                                else company
                            }
                        )
                    }
                    if (event.isFavorite && _state.value.selectedTab == ListingsTab.FAVORITES) {
                        loadWatchlistQuotes()
                    }
                }
            }
            is CompanyListingEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
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
        query: String = _state.value.searchQuery.lowercase()
    ) {
        viewModelScope.launch {
            getCompanyListings(fetchFromRemote, query).collect { result ->
                when (result) {
                    is Resource.Success -> result.data?.let { listings ->
                        _state.update { it.copy(companies = listings) }
                    }
                    is Resource.Error -> _state.update {
                        it.copy(errorMessage = result.message ?: "An unexpected error occurred")
                    }
                    is Resource.Loading -> _state.update { it.copy(isLoading = result.isLoading) }
                }
            }
            if (_state.value.selectedTab == ListingsTab.FAVORITES) loadWatchlistQuotes()
        }
    }

    private fun loadWatchlistQuotes() {
        val favorites = _state.value.companies.filter { it.isFavorite }
        if (favorites.isEmpty()) return
        viewModelScope.launch {
            val results = favorites.map { company ->
                async { company.symbol to getStockQuote(company.symbol) }
            }.awaitAll()
            val quotes = results.mapNotNull { (symbol, result) ->
                (result as? Resource.Success)?.data?.let { symbol to it }
            }.toMap()
            _state.update { it.copy(quotesBySymbol = it.quotesBySymbol + quotes) }
        }
    }
}

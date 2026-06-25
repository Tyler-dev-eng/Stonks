package com.tylerdev.stonks.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Company listings screen with search and pull-to-refresh.
 *
 * Observes [CompanyListingsViewModel.state] and dispatches [CompanyListingEvent] actions for
 * query changes and refresh. Rendered from [NavGraph] as the [CompanyListings] start destination.
 *
 * @param viewModel Hilt-provided ViewModel; defaults to [hiltViewModel] in the current back stack entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyListingsScreen(
    onCompanyClick: (symbol: String) -> Unit,
    viewModel: CompanyListingsViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(CompanyListingEvent.ErrorDismissed)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } }
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = {
                viewModel.onEvent(
                    CompanyListingEvent.OnSearchQueryChange(it)
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Search...")
            },
            maxLines = 1,
            singleLine = true
        )
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                viewModel.onEvent(CompanyListingEvent.Refresh)
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.companies.size) { i ->
                    val company = state.companies[i]
                    CompanyItem(
                        company = company,
                        onFavoriteClick = {
                            viewModel.onEvent(
                                CompanyListingEvent.ToggleFavorite(
                                    symbol = company.symbol,
                                    isFavorite = !company.isFavorite
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCompanyClick(company.symbol) }
                            .padding(16.dp)
                    )
                    if(i < state.companies.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(16.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                    }
                }
            }
        }
    }
    }
}

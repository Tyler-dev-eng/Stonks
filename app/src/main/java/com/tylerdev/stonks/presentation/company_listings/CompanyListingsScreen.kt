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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
                onValueChange = { viewModel.onEvent(CompanyListingEvent.OnSearchQueryChange(it)) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = { Text("Search...") },
                maxLines = 1,
                singleLine = true
            )

            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                Tab(
                    selected = state.selectedTab == ListingsTab.ALL,
                    onClick = { viewModel.onEvent(CompanyListingEvent.SelectTab(ListingsTab.ALL)) },
                    text = { Text("All") }
                )
                Tab(
                    selected = state.selectedTab == ListingsTab.FAVORITES,
                    onClick = { viewModel.onEvent(CompanyListingEvent.SelectTab(ListingsTab.FAVORITES)) },
                    text = { Text("Watchlist") }
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.onEvent(CompanyListingEvent.Refresh) }
            ) {
                val displayed = state.displayedCompanies
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(displayed.size) { i ->
                        val company = displayed[i]
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
                        if (i < displayed.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
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

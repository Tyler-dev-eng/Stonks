package com.tylerdev.stonks.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the company detail screen.
 *
 * Reads the ticker [symbol] from navigation [SavedStateHandle] and loads company overview and
 * intraday data in parallel via [StockRepository]. Exposes [state] for Compose UI on the
 * [CompanyDetail] route.
 */
@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
) : ViewModel() {

    /** Current screen state observed by the company detail UI. */
    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)

            when (val result = repository.getCompanyInfo(symbol)) {
                is Resource.Success -> state = state.copy(company = result.data, error = null)
                is Resource.Error -> state = state.copy(error = result.message, company = null)
                else -> Unit
            }

            if (state.company != null) {
                delay(1100L)
                when (val result = repository.getIntradayInfo(symbol)) {
                    is Resource.Success -> state = state.copy(stockInfos = result.data ?: emptyList())
                    else -> Unit
                }
            }

            state = state.copy(isLoading = false)
        }
    }
}
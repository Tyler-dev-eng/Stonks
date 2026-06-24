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

private const val RATE_LIMIT_DELAY_MS = 1100L

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
) : ViewModel() {

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
                delay(RATE_LIMIT_DELAY_MS)
                when (val result = repository.getStockQuote(symbol)) {
                    is Resource.Success -> state = state.copy(quote = result.data)
                    else -> Unit
                }
            }

            state = state.copy(isLoading = false)
        }
    }
}

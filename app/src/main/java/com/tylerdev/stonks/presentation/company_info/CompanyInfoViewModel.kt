package com.tylerdev.stonks.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerdev.stonks.domain.usecase.GetCompanyDetailUseCase
import com.tylerdev.stonks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCompanyDetail: GetCompanyDetailUseCase
) : ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            getCompanyDetail(symbol).collect { result ->
                state = when (result) {
                    is Resource.Loading -> state.copy(isLoading = result.isLoading)
                    is Resource.Success -> state.copy(
                        company = result.data?.info,
                        quote = result.data?.quote,
                        error = null,
                        isLoading = false
                    )
                    is Resource.Error -> state.copy(error = result.message, isLoading = false)
                }
            }
        }
    }
}

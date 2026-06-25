package com.tylerdev.stonks.presentation.company_info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerdev.stonks.domain.usecase.GetCompanyDetailUseCase
import com.tylerdev.stonks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCompanyDetail: GetCompanyDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CompanyInfoState())
    val state: StateFlow<CompanyInfoState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            getCompanyDetail(symbol).collect { result ->
                _state.update {
                    when (result) {
                        is Resource.Loading -> it.copy(isLoading = result.isLoading)
                        is Resource.Success -> it.copy(
                            company = result.data?.info,
                            quote = result.data?.quote,
                            isQuoteLoading = result.data?.info != null && result.data.quote == null,
                            error = null,
                            isLoading = false
                        )
                        is Resource.Error -> it.copy(error = result.message, isLoading = false)
                    }
                }
            }
        }
    }
}

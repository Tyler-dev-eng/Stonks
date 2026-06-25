package com.tylerdev.stonks.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerdev.stonks.domain.usecase.GetMarketNewsUseCase
import com.tylerdev.stonks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getMarketNews: GetMarketNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val state: StateFlow<NewsState> = _state.asStateFlow()

    init {
        loadNews()
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadNews(isRefresh = true)
    }

    private fun loadNews(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getMarketNews()) {
                is Resource.Success -> _state.update {
                    it.copy(
                        articles = result.data ?: emptyList(),
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
                is Resource.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.message ?: "Couldn't load news"
                    )
                }
                else -> Unit
            }
        }
    }
}

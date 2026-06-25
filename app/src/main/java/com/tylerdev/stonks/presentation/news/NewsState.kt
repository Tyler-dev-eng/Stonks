package com.tylerdev.stonks.presentation.news

import com.tylerdev.stonks.domain.model.NewsArticleDomainModel

data class NewsState(
    val articles: List<NewsArticleDomainModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

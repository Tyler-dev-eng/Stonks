package com.tylerdev.stonks.domain.model

data class NewsArticleDomainModel(
    val id: Long,
    val headline: String,
    val summary: String,
    val source: String,
    val imageUrl: String,
    val url: String,
    val datetimeEpochSeconds: Long,
    val category: String
)

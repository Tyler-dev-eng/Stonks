package com.tylerdev.stonks.domain.model

data class StockQuoteDomainModel(
    val current: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val previousClose: Double
)

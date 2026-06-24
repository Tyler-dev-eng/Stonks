package com.tylerdev.stonks.domain.usecase

import com.tylerdev.stonks.domain.model.StockQuoteDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import javax.inject.Inject

class GetStockQuoteUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(symbol: String): Resource<StockQuoteDomainModel> =
        repository.getStockQuote(symbol)
}

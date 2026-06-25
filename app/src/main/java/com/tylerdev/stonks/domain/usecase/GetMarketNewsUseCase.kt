package com.tylerdev.stonks.domain.usecase

import com.tylerdev.stonks.domain.model.NewsArticleDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import javax.inject.Inject

class GetMarketNewsUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): Resource<List<NewsArticleDomainModel>> =
        repository.getMarketNews()
}

package com.tylerdev.stonks.domain.repository

import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.model.NewsArticleDomainModel
import com.tylerdev.stonks.domain.model.StockQuoteDomainModel
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingDomainModel>>>

    suspend fun toggleFavorite(symbol: String, isFavorite: Boolean)

    suspend fun getStockQuote(symbol: String): Resource<StockQuoteDomainModel>

    suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfoDomainModel>

    suspend fun getMarketNews(): Resource<List<NewsArticleDomainModel>>
}

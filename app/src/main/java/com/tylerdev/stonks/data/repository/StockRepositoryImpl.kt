package com.tylerdev.stonks.data.repository

import com.tylerdev.stonks.data.local.StockDatabase
import com.tylerdev.stonks.data.remote.api.StockApi
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val stockApi: StockApi,
    private val stockDb: StockDatabase
): StockRepository {
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingDomainModel>>> {
        TODO("Not yet implemented")
    }
}
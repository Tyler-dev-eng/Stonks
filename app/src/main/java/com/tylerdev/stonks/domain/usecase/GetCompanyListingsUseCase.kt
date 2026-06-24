package com.tylerdev.stonks.domain.usecase

import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompanyListingsUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingDomainModel>>> =
        repository.getCompanyListings(fetchFromRemote, query)
}

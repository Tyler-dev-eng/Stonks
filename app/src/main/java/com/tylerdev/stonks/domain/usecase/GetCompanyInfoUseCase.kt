package com.tylerdev.stonks.domain.usecase

import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import javax.inject.Inject

class GetCompanyInfoUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(symbol: String): Resource<CompanyInfoDomainModel> =
        repository.getCompanyInfo(symbol)
}

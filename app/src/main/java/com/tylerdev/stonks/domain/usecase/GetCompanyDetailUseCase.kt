package com.tylerdev.stonks.domain.usecase

import com.tylerdev.stonks.domain.model.CompanyDetailDomainModel
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val FINNHUB_RATE_LIMIT_DELAY_MS = 1100L

class GetCompanyDetailUseCase @Inject constructor(
    private val getCompanyInfo: GetCompanyInfoUseCase,
    private val getStockQuote: GetStockQuoteUseCase
) {
    operator fun invoke(symbol: String): Flow<Resource<CompanyDetailDomainModel>> = flow {
        emit(Resource.Loading())
        when (val infoResult = getCompanyInfo(symbol)) {
            is Resource.Success -> {
                val info = infoResult.data!!
                emit(Resource.Success(CompanyDetailDomainModel(info = info)))
                delay(FINNHUB_RATE_LIMIT_DELAY_MS)
                val quote = (getStockQuote(symbol) as? Resource.Success)?.data
                emit(Resource.Success(CompanyDetailDomainModel(info = info, quote = quote)))
            }
            is Resource.Error -> emit(Resource.Error(infoResult.message ?: "Unknown error"))
            else -> Unit
        }
    }
}

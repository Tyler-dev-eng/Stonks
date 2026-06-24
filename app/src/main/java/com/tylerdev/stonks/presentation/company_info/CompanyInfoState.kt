package com.tylerdev.stonks.presentation.company_info

import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.StockQuoteDomainModel

data class CompanyInfoState(
    val company: CompanyInfoDomainModel? = null,
    val quote: StockQuoteDomainModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

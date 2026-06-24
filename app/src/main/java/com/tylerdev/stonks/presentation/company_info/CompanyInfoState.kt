package com.tylerdev.stonks.presentation.company_info

import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel

data class CompanyInfoState(
    val stockInfos: List<IntradayInfoDomainModel> = emptyList(),
    val company: CompanyInfoDomainModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

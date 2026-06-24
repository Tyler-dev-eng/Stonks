package com.tylerdev.stonks.domain.model

data class CompanyDetailDomainModel(
    val info: CompanyInfoDomainModel,
    val quote: StockQuoteDomainModel? = null
)

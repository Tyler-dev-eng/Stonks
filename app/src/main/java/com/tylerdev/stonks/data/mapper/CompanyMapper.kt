package com.tylerdev.stonks.data.mapper

import com.tylerdev.stonks.data.local.entity.CompanyListingEntity
import com.tylerdev.stonks.data.remote.dto.FinnhubProfileDto
import com.tylerdev.stonks.data.remote.dto.FinnhubQuoteDto
import com.tylerdev.stonks.data.remote.dto.FinnhubSymbolDto
import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.model.StockQuoteDomainModel

/**
 * Maps a cached [CompanyListingEntity] row into a [CompanyListingDomainModel].
 *
 * @return Domain model with listing fields copied from the stored entity.
 */
fun CompanyListingEntity.toCompanyListingDomainModel() =
    CompanyListingDomainModel(
        name = name,
        symbol = symbol,
        exchange = exchange
    )


/**
 * Maps a [CompanyListingDomainModel] into a [CompanyListingEntity] for local persistence.
 *
 * @return Entity ready for insertion or update in Room; [CompanyListingEntity.id] is left null
 *   so the database can assign a primary key on insert.
 */
fun CompanyListingDomainModel.toCompanyListingEntity() =
    CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )

fun FinnhubSymbolDto.toCompanyListingDomainModel(): CompanyListingDomainModel? {
    return CompanyListingDomainModel(
        symbol = symbol ?: return null,
        name = description ?: return null,
        exchange = mic ?: ""
    )
}

fun FinnhubProfileDto.toCompanyInfoDomainModel(): CompanyInfoDomainModel? {
    return CompanyInfoDomainModel(
        symbol = ticker ?: return null,
        name = name ?: return null,
        country = country ?: "",
        industry = industry ?: "",
        description = "",
        logoUrl = logoUrl ?: ""
    )
}

fun FinnhubQuoteDto.toStockQuoteDomainModel(): StockQuoteDomainModel? {
    return StockQuoteDomainModel(
        current = current ?: return null,
        high = high ?: return null,
        low = low ?: return null,
        open = open ?: return null,
        previousClose = previousClose ?: return null
    )
}

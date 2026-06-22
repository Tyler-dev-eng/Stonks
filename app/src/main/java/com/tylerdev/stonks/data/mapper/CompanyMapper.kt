package com.tylerdev.stonks.data.mapper

import com.tylerdev.stonks.data.local.entity.CompanyListingEntity
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel

/**
 * Maps a cached [CompanyListingEntity] row into a [CompanyListingDomainModel].
 *
 * @return Domain model with listing fields copied from the stored entity.
 */
fun CompanyListingEntity.toCompanyListingDomainModel(): CompanyListingDomainModel {
    return CompanyListingDomainModel(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}
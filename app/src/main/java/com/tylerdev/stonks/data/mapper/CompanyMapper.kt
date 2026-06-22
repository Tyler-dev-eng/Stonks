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

/**
 * Maps a [CompanyListingDomainModel] into a [CompanyListingEntity] for local persistence.
 *
 * @return Entity ready for insertion or update in Room; [CompanyListingEntity.id] is left null
 *   so the database can assign a primary key on insert.
 */
fun CompanyListingDomainModel.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

package com.tylerdev.stonks.data.mapper

import com.tylerdev.stonks.data.local.entity.CompanyListingEntity
import com.tylerdev.stonks.data.remote.dto.CompanyInfoDto
import com.tylerdev.stonks.data.remote.dto.FinnhubSymbolDto
import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel

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

fun CompanyInfoDto.toCompanyInfoDomainModel() =
    CompanyInfoDomainModel(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )

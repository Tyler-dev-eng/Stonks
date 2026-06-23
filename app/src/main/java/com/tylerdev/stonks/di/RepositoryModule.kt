package com.tylerdev.stonks.di

import com.tylerdev.stonks.data.csv.CSVParser
import com.tylerdev.stonks.data.csv.CompanyListingParser
import com.tylerdev.stonks.data.repository.StockRepositoryImpl
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding domain contracts to data-layer implementations.
 *
 * Keeps injection sites dependent on abstractions ([StockRepository], [CSVParser]) while
 * supplying concrete classes at runtime.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds [CompanyListingParser] as the [CSVParser] for [CompanyListingDomainModel] rows.
     */
    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(
        companyListingParser: CompanyListingParser
    ): CSVParser<CompanyListingDomainModel>

    /**
     * Binds [StockRepositoryImpl] as the app [StockRepository] implementation.
     */
    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}
package com.tylerdev.stonks.di

import com.tylerdev.stonks.data.csv.CSVParser
import com.tylerdev.stonks.data.csv.IntradayInfoParser
import com.tylerdev.stonks.data.repository.StockRepositoryImpl
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfoDomainModel>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}

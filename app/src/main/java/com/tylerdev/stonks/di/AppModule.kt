package com.tylerdev.stonks.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tylerdev.stonks.data.csv.CSVParser
import com.tylerdev.stonks.data.csv.CompanyListingParser
import com.tylerdev.stonks.data.local.StockDatabase
import com.tylerdev.stonks.data.remote.api.StockApi
import com.tylerdev.stonks.data.repository.StockRepositoryImpl
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindStockRepository(impl: StockRepositoryImpl): StockRepository

    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(impl: CompanyListingParser): CSVParser<CompanyListingDomainModel>

    companion object {

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                )
                .build()

        @Provides
        @Singleton
        fun provideStockApi(client: OkHttpClient): StockApi =
            Retrofit.Builder()
                .baseUrl(StockApi.BASE_URL)
                .client(client)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    )
                )
                .build()
                .create(StockApi::class.java)

        @Provides
        @Singleton
        fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase =
            Room.databaseBuilder(
                context,
                StockDatabase::class.java,
                "stock.db"
            ).build()
    }
}

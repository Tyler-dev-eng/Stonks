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

/**
 * Hilt module providing app-wide singleton dependencies.
 *
 * Wires core infrastructure: the Retrofit [StockApi] client and the Room [StockDatabase].
 * Installed in [SingletonComponent] so instances live for the application lifetime.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the Retrofit-backed Alpha Vantage API client.
     *
     * @return Configured [StockApi] using [StockApi.BASE_URL] and Moshi for JSON conversion.
     */
    @Provides
    @Singleton
    fun provideStockApi(): StockApi {
        return Retrofit.Builder()
            .baseUrl(StockApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(StockApi::class.java)
    }

    /**
     * Provides the Room database for cached company listings.
     *
     * @param context Application context used to open the on-device database file.
     * @return Singleton [StockDatabase] backed by `stonks.db`.
     */
    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase {
        return Room.databaseBuilder(
            context,
            StockDatabase::class.java,
            "stonks.db"
        ).build()
    }
}

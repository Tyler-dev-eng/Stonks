package com.tylerdev.stonks.data.repository

import com.tylerdev.stonks.data.csv.CSVParser
import com.tylerdev.stonks.data.csv.IntradayInfoParser
import com.tylerdev.stonks.data.local.StockDatabase
import com.tylerdev.stonks.data.mapper.toCompanyInfoDomainModel
import com.tylerdev.stonks.data.mapper.toCompanyListingDomainModel
import com.tylerdev.stonks.data.mapper.toCompanyListingEntity
import com.tylerdev.stonks.data.remote.api.StockApi
import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data-layer implementation of [StockRepository].
 *
 * Coordinates [StockApi], [StockDatabase], and typed [CSVParser] instances to serve listings,
 * intraday prices, and company overview data. Listings use a cache-first [Flow] strategy; intraday
 * and overview calls fetch directly from the remote service and return a single [Resource].
 */
@Singleton
class StockRepositoryImpl @Inject constructor(
    private val stockApi: StockApi,
    stockDb: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListingDomainModel>,
    private val intradayInfoParser: CSVParser<IntradayInfoDomainModel>
) : StockRepository {

    private val dao = stockDb.dao

    /**
     * @see StockRepository.getCompanyListings
     *
     * Emits cached matches for [query] first, then optionally fetches and parses the remote CSV.
     * On a successful refresh, replaces all cached rows and emits the full updated listing set.
     * Network failures emit [Resource.Error] while preserving any data already emitted.
     */
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingDomainModel>>> {
        return flow {
            emit(Resource.Loading(true))

            val localListings = dao.searchCompanyListing(query)
            emit(
                Resource.Success(
                    data = localListings.map { it.toCompanyListingDomainModel() }
                )
            )

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = stockApi.getListings()
                companyListingParser.parser(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(
                    Resource.Success(
                        data = dao
                            .searchCompanyListing("")
                            .map { it.toCompanyListingDomainModel() }
                    )
                )
                emit(Resource.Loading(false))
            }
        }
    }

    /**
     * @see StockRepository.getIntradayInfo
     *
     * Fetches hourly intraday CSV for [symbol], parses it via [intradayInfoParser], and returns
     * the filtered, sorted domain models. Network failures yield [Resource.Error].
     *
     * @param symbol Ticker symbol to query (e.g. AAPL).
     */
    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfoDomainModel>> {
       return try {
           val response = stockApi.getIntradayInfo(symbol)
           val results = intradayInfoParser.parser(response.byteStream())
           Resource.Success(results)
       } catch (e: IOException) {
           e.printStackTrace()
           Resource.Error(message = "Couldn't load intraday data")
       } catch (e: HttpException) {
           e.printStackTrace()
           Resource.Error(message = "Couldn't load intraday data")
       }
    }

    /**
     * @see StockRepository.getCompanyInfo
     *
     * Fetches the Alpha Vantage overview for [symbol] and maps the response to
     * [CompanyInfoDomainModel]. Network failures yield [Resource.Error].
     *
     * @param symbol Ticker symbol to query (e.g. AAPL).
     */
    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfoDomainModel> {
        return try {
            val result = stockApi.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfoDomainModel())
        }  catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "Couldn't load company data")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(message = "Couldn't load company data")
        }
    }
}
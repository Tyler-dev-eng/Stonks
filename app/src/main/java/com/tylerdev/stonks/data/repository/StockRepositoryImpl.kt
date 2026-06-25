package com.tylerdev.stonks.data.repository

import com.tylerdev.stonks.data.local.StockDatabase
import com.tylerdev.stonks.data.mapper.toCompanyInfoDomainModel
import com.tylerdev.stonks.data.mapper.toCompanyListingDomainModel
import com.tylerdev.stonks.data.mapper.toCompanyListingEntity
import com.tylerdev.stonks.data.mapper.toNewsArticleDomainModel
import com.tylerdev.stonks.data.mapper.toStockQuoteDomainModel
import com.tylerdev.stonks.data.remote.api.FinnhubApi
import com.tylerdev.stonks.domain.model.CompanyInfoDomainModel
import com.tylerdev.stonks.domain.model.CompanyListingDomainModel
import com.tylerdev.stonks.domain.model.NewsArticleDomainModel
import com.tylerdev.stonks.domain.model.StockQuoteDomainModel
import com.tylerdev.stonks.domain.repository.StockRepository
import com.tylerdev.stonks.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val finnhubApi: FinnhubApi,
    stockDb: StockDatabase
) : StockRepository {

    private val dao = stockDb.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingDomainModel>>> {
        return flow {
            emit(Resource.Loading(true))

            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(data = localListings.map { it.toCompanyListingDomainModel() }))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                finnhubApi.getListings().mapNotNull { it.toCompanyListingDomainModel() }
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
                val favoritedSymbols = dao.getFavoritedSymbols()
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })
                if (favoritedSymbols.isNotEmpty()) {
                    dao.restoreFavorites(favoritedSymbols)
                }
                emit(
                    Resource.Success(
                        data = dao.searchCompanyListing("").map { it.toCompanyListingDomainModel() }
                    )
                )
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun toggleFavorite(symbol: String, isFavorite: Boolean) {
        dao.toggleFavorite(symbol, isFavorite)
    }

    override suspend fun getStockQuote(symbol: String): Resource<StockQuoteDomainModel> {
        return try {
            val quote = finnhubApi.getQuote(symbol).toStockQuoteDomainModel()
                ?: return Resource.Error("No quote data available for $symbol")
            Resource.Success(quote)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error("Couldn't load quote data")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error("Couldn't load quote data")
        }
    }

    override suspend fun getMarketNews(): Resource<List<NewsArticleDomainModel>> {
        return try {
            val articles = finnhubApi.getMarketNews().mapNotNull { it.toNewsArticleDomainModel() }
            Resource.Success(articles)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error("Couldn't load news")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error("Couldn't load news")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfoDomainModel> {
        return try {
            val result = finnhubApi.getCompanyProfile(symbol)
            val domainModel = result.toCompanyInfoDomainModel()
                ?: return Resource.Error("No data found for $symbol")
            Resource.Success(domainModel)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error("Couldn't load company data")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error("Couldn't load company data")
        }
    }
}

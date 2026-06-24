package com.tylerdev.stonks.data.remote.api

import com.tylerdev.stonks.BuildConfig
import com.tylerdev.stonks.data.remote.dto.FinnhubSymbolDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {
    companion object {
        const val BASE_URL = "https://finnhub.io/api/v1/"
    }

    @GET("stock/symbol")
    suspend fun getListings(
        @Query("exchange") exchange: String = "US",
        @Query("token") token: String = BuildConfig.FINNHUB_API_KEY
    ): List<FinnhubSymbolDto>
}

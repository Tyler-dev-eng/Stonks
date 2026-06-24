package com.tylerdev.stonks.data.remote.api

import com.tylerdev.stonks.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    companion object {
        const val BASE_URL = "https://alphavantage.co"
    }

    @GET("query?function=TIME_SERIES_DAILY&datatype=csv")
    suspend fun getIntradayInfo(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = BuildConfig.ALPHA_VANTAGE_API_KEY
    ): ResponseBody
}

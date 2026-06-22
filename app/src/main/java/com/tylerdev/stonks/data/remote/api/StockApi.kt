package com.tylerdev.stonks.data.remote.api

import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    companion object {
        const val BASE_URL = "https://alphavantage.co"
    }

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(@Query("apikey") apiKey: String): ResponseBody
}
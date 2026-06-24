package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

data class FinnhubProfileDto(
    @Json(name = "ticker") val ticker: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "country") val country: String?,
    @Json(name = "finnhubIndustry") val industry: String?
)

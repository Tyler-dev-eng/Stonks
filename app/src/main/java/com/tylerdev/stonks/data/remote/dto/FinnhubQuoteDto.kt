package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

data class FinnhubQuoteDto(
    @Json(name = "c") val current: Double?,
    @Json(name = "h") val high: Double?,
    @Json(name = "l") val low: Double?,
    @Json(name = "o") val open: Double?,
    @Json(name = "pc") val previousClose: Double?
)

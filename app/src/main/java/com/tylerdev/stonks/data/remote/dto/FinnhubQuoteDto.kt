package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

data class FinnhubQuoteDto(
    @param:Json(name = "c") val current: Double?,
    @param:Json(name = "h") val high: Double?,
    @param:Json(name = "l") val low: Double?,
    @param:Json(name = "o") val open: Double?,
    @param:Json(name = "pc") val previousClose: Double?
)

package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

data class FinnhubSymbolDto(
    @Json(name = "symbol") val symbol: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "mic") val mic: String?
)

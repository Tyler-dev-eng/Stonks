package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

data class FinnhubSymbolDto(
    @param:Json(name = "symbol") val symbol: String?,
    @param:Json(name = "description") val description: String?,
    @param:Json(name = "mic") val mic: String?
)

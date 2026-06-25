package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

data class FinnhubProfileDto(
    @param:Json(name = "ticker") val ticker: String?,
    @param:Json(name = "name") val name: String?,
    @param:Json(name = "country") val country: String?,
    @param:Json(name = "finnhubIndustry") val industry: String?,
    @param:Json(name = "logo") val logoUrl: String?
)

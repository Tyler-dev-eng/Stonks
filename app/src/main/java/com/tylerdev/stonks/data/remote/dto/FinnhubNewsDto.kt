package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

data class FinnhubNewsDto(
    @param:Json(name = "id") val id: Long?,
    @param:Json(name = "headline") val headline: String?,
    @param:Json(name = "summary") val summary: String?,
    @param:Json(name = "source") val source: String?,
    @param:Json(name = "image") val image: String?,
    @param:Json(name = "url") val url: String?,
    @param:Json(name = "datetime") val datetime: Long?,
    @param:Json(name = "category") val category: String?
)

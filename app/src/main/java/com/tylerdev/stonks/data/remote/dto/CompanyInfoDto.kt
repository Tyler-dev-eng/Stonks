package com.tylerdev.stonks.data.remote.dto

import com.squareup.moshi.Json

/**
 * Moshi DTO for the Alpha Vantage company overview response.
 *
 * Mirrors the JSON field names returned by the overview endpoint. Repositories deserialize this
 * type from network responses and map it into [CompanyInfoDomainModel].
 */
data class CompanyInfoDto(
    /** Ticker symbol; JSON key `Symbol`. */
    @field:Json(name = "Symbol") val symbol: String?,
    /** Business description; JSON key `Description`. */
    @field:Json(name = "Description") val description: String?,
    /** Company name; JSON key `Name`. */
    @field:Json(name = "Name") val name: String?,
    /** Headquarters country; JSON key `Country`. */
    @field:Json(name = "Country") val country: String?,
    /** Industry sector; JSON key `Industry`. */
    @field:Json(name = "Industry") val industry: String?
)

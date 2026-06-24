package com.tylerdev.stonks.data.mapper

import com.tylerdev.stonks.data.remote.dto.IntradayInfoDto
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Maps an [IntradayInfoDto] into an [IntradayInfoDomainModel].
 *
 * Parses [IntradayInfoDto.timestamp] using the `yyyy-MM-dd HH:mm:ss` pattern.
 *
 * @return Domain model with a typed [IntradayInfoDomainModel.date] and the same close price.
 */
fun IntradayInfoDto.toIntradayInfoDomainModel(): IntradayInfoDomainModel {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)

    return IntradayInfoDomainModel(
        date = localDateTime,
        close = close
    )
}

/**
 * Maps an [IntradayInfoDomainModel] into an [IntradayInfoDto].
 *
 * Formats [IntradayInfoDomainModel.date] as `yyyy-MM-dd HH:mm:ss` for API or CSV compatibility.
 *
 * @return DTO with a string timestamp and unchanged close price.
 */
fun IntradayInfoDomainModel.toIntradayInfoDto(): IntradayInfoDto {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

    return IntradayInfoDto(
        timestamp = date.format(formatter),
        close = close
    )
}


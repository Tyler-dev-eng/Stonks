package com.tylerdev.stonks.data.mapper

import com.tylerdev.stonks.data.remote.dto.IntradayInfoDto
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

fun IntradayInfoDto.toIntradayInfoDomainModel(): IntradayInfoDomainModel {
    val date = LocalDate.parse(timestamp, DATE_FORMATTER).atStartOfDay()
    return IntradayInfoDomainModel(date = date, close = close)
}

fun IntradayInfoDomainModel.toIntradayInfoDto(): IntradayInfoDto {
    return IntradayInfoDto(
        timestamp = date.format(DATE_FORMATTER),
        close = close
    )
}


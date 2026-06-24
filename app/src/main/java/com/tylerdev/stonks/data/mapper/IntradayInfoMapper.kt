package com.tylerdev.stonks.data.mapper

import com.tylerdev.stonks.data.remote.dto.IntradayInfoDto
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun IntradayInfoDto.toIntradayInfoDomainModel(): IntradayInfoDomainModel {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)

    return IntradayInfoDomainModel(
        date = localDateTime,
        close = close
    )
}

fun IntradayInfoDomainModel.toIntradayInfoDto() : IntradayInfoDto {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

    return IntradayInfoDto(
        timestamp = date.format(formatter),
        close = close
    )
}


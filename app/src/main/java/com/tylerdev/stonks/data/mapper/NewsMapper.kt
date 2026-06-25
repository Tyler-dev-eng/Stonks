package com.tylerdev.stonks.data.mapper

import com.tylerdev.stonks.data.remote.dto.FinnhubNewsDto
import com.tylerdev.stonks.domain.model.NewsArticleDomainModel

fun FinnhubNewsDto.toNewsArticleDomainModel(): NewsArticleDomainModel? {
    return NewsArticleDomainModel(
        id = id ?: return null,
        headline = headline?.takeIf { it.isNotBlank() } ?: return null,
        url = url?.takeIf { it.isNotBlank() } ?: return null,
        summary = summary ?: "",
        source = source ?: "",
        imageUrl = image ?: "",
        datetimeEpochSeconds = datetime ?: 0L,
        category = category ?: ""
    )
}

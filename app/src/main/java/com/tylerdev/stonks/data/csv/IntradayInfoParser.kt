package com.tylerdev.stonks.data.csv

import com.opencsv.CSVReader
import com.tylerdev.stonks.data.mapper.toIntradayInfoDomainModel
import com.tylerdev.stonks.data.remote.dto.IntradayInfoDto
import com.tylerdev.stonks.domain.model.IntradayInfoDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfoDomainModel> {
    override suspend fun parser(stream: InputStream): List<IntradayInfoDomainModel> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    val dto = IntradayInfoDto(timestamp, close.toDouble())
                    dto.toIntradayInfoDomainModel()
                }.filter {
                    it.date.dayOfMonth == LocalDateTime.now().minusDays(4).dayOfMonth
                }.sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}
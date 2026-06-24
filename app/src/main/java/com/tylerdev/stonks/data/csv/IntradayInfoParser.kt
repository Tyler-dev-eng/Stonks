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

/**
 * [CSVParser] implementation for Alpha Vantage intraday time-series CSV exports.
 *
 * Parses intraday CSV rows into [IntradayInfoDomainModel] via [IntradayInfoDto]. Expects
 * timestamp at column 0 and close price at column 4. The header row is skipped; rows with
 * missing fields are omitted. Results are filtered to a target trading day and sorted by hour.
 */
@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfoDomainModel> {

    /**
     * Reads intraday CSV from the stream and maps each data row to a domain model.
     *
     * Parsing runs on [Dispatchers.IO]. The first row is treated as a header and discarded.
     *
     * @param stream Raw CSV from the intraday time-series endpoint.
     * @return Parsed intraday observations for the filtered day, ordered by hour.
     */
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